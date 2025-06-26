package com.mayosen.financeapp.integration

import com.mayosen.financeapp.event.AccountCreatedEvent
import com.mayosen.financeapp.event.DepositPerformedEvent
import com.mayosen.financeapp.event.WithdrawalPerformedEvent
import com.mayosen.financeapp.event.jdbc.EventEntity
import com.mayosen.financeapp.event.serialization.typeName
import com.mayosen.financeapp.test.ACCOUNT_ID
import com.mayosen.financeapp.test.AMOUNT_50
import com.mayosen.financeapp.test.EVENT_ID_3
import com.mayosen.financeapp.test.OWNER_ID
import com.mayosen.financeapp.test.SEQUENCE_NUMBER_2
import com.mayosen.financeapp.test.SEQUENCE_NUMBER_3
import com.mayosen.financeapp.test.assertions.assertHasType
import com.mayosen.financeapp.test.assertions.assertHasValueOfType
import com.mayosen.financeapp.test.assertions.isCloseToNow
import com.mayosen.financeapp.test.context.BaseIntegrationTest
import com.mayosen.financeapp.test.generator.generateCommandResponse
import com.mayosen.financeapp.test.generator.generateWithdrawRequest
import net.javacrumbs.jsonunit.spring.JsonUnitResultMatchers.json
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class WithdrawCommandIT : BaseIntegrationTest() {
    @Test
    fun `happy path - should withdraw money`() {
        // given: account created
        val accountCreatedEvent = AccountCreatedEvent(idGenerator.generateEventId(), ACCOUNT_ID, ownerId = OWNER_ID)
        jdbcTemplate.save(eventEntityOf(accountCreatedEvent))

        // given: deposit performed
        val depositPerformedEvent = DepositPerformedEvent(idGenerator.generateEventId(), ACCOUNT_ID, amount = AMOUNT_50)
        jdbcTemplate.save(eventEntityOf(depositPerformedEvent, SEQUENCE_NUMBER_2))

        // given
        val requestBody = stringBodyOf(generateWithdrawRequest())

        // step: request
        val resultActions =
            mockMvc.perform(
                post("/command-api/v1/withdraw")
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
        assertThat(events).hasSize(3)

        val withdrawPerformedEvent = events.first { it.eventType == WithdrawalPerformedEvent::class.typeName() }
        assertThat(withdrawPerformedEvent.eventId).isEqualTo(EVENT_ID_3)
        assertThat(withdrawPerformedEvent.sequenceNumber).isEqualTo(SEQUENCE_NUMBER_3)
        assertThat(withdrawPerformedEvent.accountId).isEqualTo(ACCOUNT_ID)
        assertThat(withdrawPerformedEvent.eventFields).isEqualTo(eventFieldsOf(AMOUNT_FIELD to AMOUNT_50))
        assertThat(withdrawPerformedEvent.timestamp).isCloseToNow()
        assertThat(withdrawPerformedEvent.isNewEntity).isFalse()

        // step: verify event published
        val record = eventsCustomer.pollNotNull()

        assertThat(record.key()).isEqualTo(EVENT_ID_3)
        record.headers().assertHasType(WithdrawalPerformedEvent::class)

        val event = record.assertHasValueOfType(WithdrawalPerformedEvent::class)
        assertThat(event.eventId).isEqualTo(EVENT_ID_3)
        assertThat(event.accountId).isEqualTo(ACCOUNT_ID)
        assertThat(event.timestamp).isCloseToNow()
        assertThat(event.amount).isEqualTo(AMOUNT_50)
    }
}
