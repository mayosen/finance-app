package com.mayosen.financeapp.integration.command

import com.mayosen.financeapp.event.AccountCreatedEvent
import com.mayosen.financeapp.event.AccountDeletedEvent
import com.mayosen.financeapp.event.jdbc.EventEntity
import com.mayosen.financeapp.event.serialization.typeName
import com.mayosen.financeapp.snapshot.jdbc.AccountSnapshotEntity
import com.mayosen.financeapp.test.ACCOUNT_ID
import com.mayosen.financeapp.test.AMOUNT_0
import com.mayosen.financeapp.test.EVENT_ID_2
import com.mayosen.financeapp.test.OWNER_ID
import com.mayosen.financeapp.test.SEQUENCE_NUMBER
import com.mayosen.financeapp.test.SEQUENCE_NUMBER_2
import com.mayosen.financeapp.test.assertions.assertHasType
import com.mayosen.financeapp.test.assertions.assertHasValueOfType
import com.mayosen.financeapp.test.assertions.isCloseToNow
import com.mayosen.financeapp.test.context.BaseIntegrationTest
import com.mayosen.financeapp.test.generator.generateCommandResponse
import com.mayosen.financeapp.test.generator.generateDeleteAccountRequest
import net.javacrumbs.jsonunit.spring.JsonUnitResultMatchers.json
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class DeleteAccountCommandIT : BaseIntegrationTest() {
    @Test
    fun `happy path - should delete account`() {
        // given: account created
        val accountCreatedEvent = AccountCreatedEvent(idGenerator.generateEventId(), ACCOUNT_ID, ownerId = OWNER_ID)
        jdbcTemplate.save(eventEntityOf(accountCreatedEvent))

        // given: snapshot created
        val snapshot =
            AccountSnapshotEntity(
                accountId = ACCOUNT_ID,
                balance = AMOUNT_0,
                lastSequenceNumber = SEQUENCE_NUMBER,
                isNewEntity = true,
            )
        jdbcTemplate.save(snapshot)

        // given
        val requestBody = stringBodyOf(generateDeleteAccountRequest())

        // step: request
        val resultActions =
            mockMvc.perform(
                delete("/command-api/v1/accounts")
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
        assertThat(events).hasSize(2)

        val accountDeletedEvent = events.first { it.eventType == AccountDeletedEvent::class.typeName() }
        assertThat(accountDeletedEvent.eventId).isEqualTo(EVENT_ID_2)
        assertThat(accountDeletedEvent.sequenceNumber).isEqualTo(SEQUENCE_NUMBER_2)
        assertThat(accountDeletedEvent.accountId).isEqualTo(ACCOUNT_ID)
        assertThat(accountDeletedEvent.eventType).isEqualTo(AccountDeletedEvent::class.typeName())
        assertThat(accountDeletedEvent.eventFields).isEqualTo(eventFieldsOf())
        assertThat(accountDeletedEvent.timestamp).isCloseToNow()
        assertThat(accountDeletedEvent.isNewEntity).isFalse()

        // step: verify snapshot deleted
        val snapshots = jdbcTemplate.findAll(AccountSnapshotEntity::class.java)
        assertThat(snapshots).isEmpty()

        // step: verify event published
        val record = eventsCustomer.pollNotNull()
        assertThat(record.key()).isEqualTo(EVENT_ID_2)
        record.headers().assertHasType(AccountDeletedEvent::class)

        val event = record.assertHasValueOfType(AccountDeletedEvent::class)
        assertThat(event.eventId).isEqualTo(EVENT_ID_2)
        assertThat(event.accountId).isEqualTo(ACCOUNT_ID)
        assertThat(event.timestamp).isCloseToNow()
    }
}
