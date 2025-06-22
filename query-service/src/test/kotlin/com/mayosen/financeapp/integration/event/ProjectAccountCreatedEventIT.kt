package com.mayosen.financeapp.integration.event

import com.mayosen.financeapp.event.AccountCreatedEvent
import com.mayosen.financeapp.event.Event
import com.mayosen.financeapp.event.serialization.EVENT_TYPE_KEY
import com.mayosen.financeapp.event.serialization.typeName
import com.mayosen.financeapp.readmodel.accountsummary.jdbc.AccountSummaryEntity
import com.mayosen.financeapp.test.ACCOUNT_ID
import com.mayosen.financeapp.test.BALANCE_0
import com.mayosen.financeapp.test.EVENT_ID
import com.mayosen.financeapp.test.OWNER_ID
import com.mayosen.financeapp.test.context.BaseIntegrationTest
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.header.Header
import org.apache.kafka.common.header.internals.RecordHeader
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.byLessThan
import org.awaitility.kotlin.atMost
import org.awaitility.kotlin.await
import org.awaitility.kotlin.untilAsserted
import org.awaitility.kotlin.withPollDelay
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.Instant
import java.time.temporal.ChronoUnit

class ProjectAccountCreatedEventIT : BaseIntegrationTest() {
    @Test
    fun `happy path - should project AccountCreatedEvent`() {
        // given
        val event =
            AccountCreatedEvent(
                eventId = EVENT_ID,
                accountId = ACCOUNT_ID,
                ownerId = OWNER_ID,
            )

        // step: publish
        eventKafkaTemplate.send(event.toProducerRecord())

        // step: verify AccountSummary saved
        await atMost SECONDS_5 withPollDelay MILLIS_200 untilAsserted {
            val entities = jdbcTemplate.findAll(AccountSummaryEntity::class.java)
            assertThat(entities).hasSize(1)

            val entity = entities.first()
            assertThat(entity.accountId).isEqualTo(ACCOUNT_ID)
            assertThat(entity.ownerId).isEqualTo(OWNER_ID)
            assertThat(entity.balance).isEqualTo(BALANCE_0)
            assertThat(entity.updatedAt).isCloseTo(Instant.now(), byLessThan(1, ChronoUnit.SECONDS))
            assertThat(entity.isNewEntity).isFalse()
        }
    }

    fun Event.toProducerRecord(): ProducerRecord<String, String> {
        val payload = objectMapper.writeValueAsString(this)
        val headers = listOf(createHeader(EVENT_TYPE_KEY, typeName()))
        return ProducerRecord("financeapp-events", null, eventId, payload, headers)
    }

    fun createHeader(
        key: String,
        value: String,
    ): Header = RecordHeader(key, value.toByteArray())

    private companion object {
        val SECONDS_5: Duration = Duration.ofSeconds(5)
        val MILLIS_200: Duration = Duration.ofMillis(200)
    }
}
