package com.mayosen.financeapp.integration

import com.mayosen.financeapp.event.AccountCreatedEvent
import com.mayosen.financeapp.event.DepositPerformedEvent
import com.mayosen.financeapp.event.jdbc.EventEntity
import com.mayosen.financeapp.event.serialization.typeName
import com.mayosen.financeapp.snapshot.jdbc.AccountSnapshotEntity
import com.mayosen.financeapp.test.ACCOUNT_ID
import com.mayosen.financeapp.test.AMOUNT_100
import com.mayosen.financeapp.test.EVENT_ID_2
import com.mayosen.financeapp.test.OWNER_ID
import com.mayosen.financeapp.test.SEQUENCE_NUMBER_2
import com.mayosen.financeapp.test.assertions.assertHasType
import com.mayosen.financeapp.test.assertions.assertHasValueOfType
import com.mayosen.financeapp.test.context.BaseIntegrationTest
import com.mayosen.financeapp.test.generator.generateCommandResponse
import com.mayosen.financeapp.test.generator.generateDepositRequest
import net.javacrumbs.jsonunit.spring.JsonUnitResultMatchers.json
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.byLessThan
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.Instant
import java.time.temporal.ChronoUnit

class DepositCommandIT : BaseIntegrationTest() {
    @Test
    fun `happy path - should deposit money`() {
        // given: account created
        val accountCreatedEvent = AccountCreatedEvent(idGenerator.generateEventId(), ACCOUNT_ID, ownerId = OWNER_ID)
        jdbcTemplate.save(eventEntityOf(accountCreatedEvent))

        // given
        val requestBody = stringBodyOf(generateDepositRequest())

        // step: request
        val resultActions =
            mockMvc.perform(
                post("/command-api/v1/deposit")
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

        val depositPerformedEvent = events.first { it.eventType == DepositPerformedEvent::class.typeName() }

        assertThat(depositPerformedEvent.eventId).isEqualTo(EVENT_ID_2)
        assertThat(depositPerformedEvent.sequenceNumber).isEqualTo(SEQUENCE_NUMBER_2)
        assertThat(depositPerformedEvent.accountId).isEqualTo(ACCOUNT_ID)
        assertThat(depositPerformedEvent.eventFields).isEqualTo(eventFieldsOf(AMOUNT_FIELD to AMOUNT_100))
        assertThat(depositPerformedEvent.timestamp).isCloseTo(Instant.now(), byLessThan(1, ChronoUnit.SECONDS))
        assertThat(depositPerformedEvent.isNewEntity).isFalse()

        // step: verify snapshot not saved
        val snapshots = jdbcTemplate.findAll(AccountSnapshotEntity::class.java)
        assertThat(snapshots).isEmpty()

        // step: verify event published
        val record = eventsCustomer.pollNotNull()

        assertThat(record.key()).isEqualTo(EVENT_ID_2)
        record.headers().assertHasType(DepositPerformedEvent::class)

        val event = record.assertHasValueOfType(DepositPerformedEvent::class)
        assertThat(event.eventId).isEqualTo(EVENT_ID_2)
        assertThat(event.accountId).isEqualTo(ACCOUNT_ID)
        assertThat(event.timestamp).isCloseTo(Instant.now(), byLessThan(1, ChronoUnit.SECONDS))
        assertThat(event.amount).isEqualTo(AMOUNT_100)
    }
}
