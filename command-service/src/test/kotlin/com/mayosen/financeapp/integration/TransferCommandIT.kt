package com.mayosen.financeapp.integration

import com.mayosen.financeapp.event.AccountCreatedEvent
import com.mayosen.financeapp.event.DepositPerformedEvent
import com.mayosen.financeapp.event.TransferPerformedEvent
import com.mayosen.financeapp.event.jdbc.EventEntity
import com.mayosen.financeapp.event.serialization.typeName
import com.mayosen.financeapp.test.ACCOUNT_ID
import com.mayosen.financeapp.test.ACCOUNT_ID_2
import com.mayosen.financeapp.test.AMOUNT_100
import com.mayosen.financeapp.test.AMOUNT_50
import com.mayosen.financeapp.test.EVENT_ID_4
import com.mayosen.financeapp.test.OWNER_ID
import com.mayosen.financeapp.test.SEQUENCE_NUMBER_2
import com.mayosen.financeapp.test.SEQUENCE_NUMBER_3
import com.mayosen.financeapp.test.assertions.assertHasType
import com.mayosen.financeapp.test.assertions.assertHasValueOfType
import com.mayosen.financeapp.test.assertions.isCloseToNow
import com.mayosen.financeapp.test.context.BaseIntegrationTest
import com.mayosen.financeapp.test.generator.generateCommandResponse
import com.mayosen.financeapp.test.generator.generateTransferRequest
import net.javacrumbs.jsonunit.spring.JsonUnitResultMatchers.json
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class TransferCommandIT : BaseIntegrationTest() {
    @Test
    fun `happy path - should transfer money`() {
        // given: account 1 created
        val account1Created = AccountCreatedEvent(idGenerator.generateEventId(), ACCOUNT_ID, ownerId = OWNER_ID)
        jdbcTemplate.save(eventEntityOf(account1Created))

        // given: deposit 1 performed
        val deposit1PerformedEvent =
            DepositPerformedEvent(idGenerator.generateEventId(), ACCOUNT_ID, amount = AMOUNT_100)
        jdbcTemplate.save(eventEntityOf(deposit1PerformedEvent, SEQUENCE_NUMBER_2))

        // given: account 2 created
        val account2CreatedEvent = AccountCreatedEvent(idGenerator.generateEventId(), ACCOUNT_ID_2, ownerId = OWNER_ID)
        jdbcTemplate.save(eventEntityOf(account2CreatedEvent, SEQUENCE_NUMBER_3))

        // given
        val requestBody = stringBodyOf(generateTransferRequest())

        // step: request
        val resultActions =
            mockMvc.perform(
                post("/command-api/v1/transfer")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody),
            )

        // step: verify response
        val responseBody = stringBodyOf(generateCommandResponse())
        resultActions
            .andExpect(status().isOk)
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(json().isEqualTo(responseBody))

        // step: verify event saved
        val events = jdbcTemplate.findAll(EventEntity::class.java)
        assertThat(events).hasSize(4)

        val withdrawPerformedEvent = events.first { it.eventType == TransferPerformedEvent::class.typeName() }
        assertThat(withdrawPerformedEvent.eventId).isEqualTo(EVENT_ID_4)
        assertThat(withdrawPerformedEvent.sequenceNumber).isEqualTo(SEQUENCE_NUMBER_3)
        assertThat(withdrawPerformedEvent.accountId).isEqualTo(ACCOUNT_ID)
        val eventFieldsOf = eventFieldsOf(AMOUNT_FIELD to AMOUNT_50, TO_ACCOUNT_ID_FIELD to ACCOUNT_ID_2)
        assertThat(withdrawPerformedEvent.eventFields).isEqualTo(eventFieldsOf)
        assertThat(withdrawPerformedEvent.timestamp).isCloseToNow()
        assertThat(withdrawPerformedEvent.isNewEntity).isFalse()

        // step: verify event published
        val record = eventsCustomer.pollNotNull()

        assertThat(record.key()).isEqualTo(EVENT_ID_4)
        record.headers().assertHasType(TransferPerformedEvent::class)

        val event = record.assertHasValueOfType(TransferPerformedEvent::class)
        assertThat(event.eventId).isEqualTo(EVENT_ID_4)
        assertThat(event.accountId).isEqualTo(ACCOUNT_ID)
        assertThat(event.timestamp).isCloseToNow()
        assertThat(event.amount).isEqualTo(AMOUNT_50)
        assertThat(event.toAccountId).isEqualTo(ACCOUNT_ID_2)
    }
}
