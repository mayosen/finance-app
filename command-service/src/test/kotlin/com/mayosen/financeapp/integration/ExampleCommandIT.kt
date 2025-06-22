package com.mayosen.financeapp.integration

import com.mayosen.financeapp.event.AccountCreatedEvent
import com.mayosen.financeapp.event.jdbc.EventEntity
import com.mayosen.financeapp.snapshot.jdbc.AccountSnapshotEntity
import com.mayosen.financeapp.test.context.BaseIntegrationTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.concurrent.TimeUnit

class ExampleCommandIT : BaseIntegrationTest() {
    @Test
    fun test() {
        // given
        val requestBody = """{"ownerId":"mayosen"}"""

        // step: request
        val resultActions =
            mockMvc.perform(
                post("/command-api/v1/accounts")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody),
            )

        // step: verify response

        // TODO: Verify response
        resultActions
            .andExpect(status().isOk)
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))

        // step: verify event saved
        val events = jdbcTemplate.findAll(EventEntity::class.java)
        assertThat(events).hasSize(1)

        val entity = events[0]
        assertThat(entity.eventId).isNotBlank()
        assertThat(entity.sequenceNumber).isEqualTo(1)
        assertThat(entity.accountId).isNotBlank()
        // assertThat(entity.eventType).isEqualTo(AccountCreatedEvent)
        assertThat(entity.eventFields).isNotBlank() // TODO
        // assertThat(entity.timestamp).isCloseTo(Instant.now(), byLessThan(1, ChronoUnit.SECONDS))
        assertThat(entity.isNewEntity).isFalse()

        // step: verify snapshot not created
        val snapshots = jdbcTemplate.findAll(AccountSnapshotEntity::class.java)
        assertThat(snapshots).isEmpty()

        // step: verify event published
        val record = eventsCustomer.records.poll(5, TimeUnit.SECONDS)
        assertThat(record).isNotNull

        val recordValue = record!!.value()
        assertThat(recordValue).isExactlyInstanceOf(AccountCreatedEvent::class.java)

        val event = recordValue as AccountCreatedEvent
        assertThat(event.eventId).isNotBlank()
        assertThat(event.accountId).isNotBlank()
        // assertThat(event.timestamp).isCloseTo(Instant.now(), byLessThan(1, ChronoUnit.SECONDS))
        assertThat(event.ownerId).isEqualTo("mayosen")
    }
}
