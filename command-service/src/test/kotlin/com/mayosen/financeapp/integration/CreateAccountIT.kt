package com.mayosen.financeapp.integration

import com.mayosen.financeapp.event.AccountCreatedEvent
import com.mayosen.financeapp.event.jdbc.EventEntity
import com.mayosen.financeapp.event.serialization.typeName
import com.mayosen.financeapp.snapshot.jdbc.AccountSnapshotEntity
import com.mayosen.financeapp.test.OWNER_ID
import com.mayosen.financeapp.test.context.BaseIntegrationTest
import com.mayosen.financeapp.test.generator.generateCommandResponse
import com.mayosen.financeapp.test.generator.generateCreateAccountRequest
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

class CreateAccountIT : BaseIntegrationTest() {
    @Test
    fun `happy path - should create account`() {
        // given
        val requestBody = stringBodyOf(generateCreateAccountRequest())

        // step: request
        val resultActions =
            mockMvc.perform(
                post("/command-api/v1/accounts")
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
        assertThat(events).hasSize(1)

        val entity = events.first()
        assertThat(entity.eventId).isNotBlank()
        assertThat(entity.sequenceNumber).isEqualTo(1)
        assertThat(entity.accountId).isNotBlank()
        assertThat(entity.eventType).isEqualTo(AccountCreatedEvent::class.typeName())
        assertThat(entity.eventFields).isNotBlank()
        assertThat(entity.timestamp).isCloseTo(Instant.now(), byLessThan(1, ChronoUnit.SECONDS))
        assertThat(entity.isNewEntity).isFalse()

        // step: verify snapshot not saved
        val snapshots = jdbcTemplate.findAll(AccountSnapshotEntity::class.java)
        assertThat(snapshots).isEmpty()

        // step: verify event published
        val record = eventsCustomer.pollNotNull()

        val value = record.value()
        assertThat(value).isExactlyInstanceOf(AccountCreatedEvent::class.java)

        val event = value as AccountCreatedEvent
        assertThat(event.eventId).isNotBlank()
        assertThat(event.accountId).isNotBlank()
        assertThat(event.timestamp).isCloseTo(Instant.now(), byLessThan(1, ChronoUnit.SECONDS))
        assertThat(event.ownerId).isEqualTo(OWNER_ID)
    }
}
