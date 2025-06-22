package com.mayosen.financeapp.integration

import com.mayosen.financeapp.event.AccountCreatedEvent
import com.mayosen.financeapp.event.EVENT_TYPE_KEY
import com.mayosen.financeapp.event.Event
import com.mayosen.financeapp.event.typeName
import com.mayosen.financeapp.test.context.BaseIntegrationTest
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.header.Header
import org.apache.kafka.common.header.internals.RecordHeader
import org.junit.jupiter.api.Test

class ExampleQueryIT : BaseIntegrationTest() {
    @Test
    fun test() {
        // step: publish
        val event =
            AccountCreatedEvent(
                eventId = "event_id",
                accountId = "account_id",
                ownerId = "owner_id",
            )

        eventKafkaTemplate.send(event.toProducerRecord())

        // step: listen
        Thread.sleep(1000000)
        // TODO: Verify answer
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
}
