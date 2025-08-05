package com.mayosen.financeapp.event.kafka

import com.fasterxml.jackson.databind.ObjectMapper
import com.mayosen.financeapp.event.Event
import com.mayosen.financeapp.event.EventPublisher
import com.mayosen.financeapp.event.serialization.EVENT_TYPE_KEY
import com.mayosen.financeapp.event.serialization.typeName
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.header.Header
import org.apache.kafka.common.header.internals.RecordHeader
import org.apache.logging.log4j.kotlin.Logging
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class KafkaEventPublisher(
    @Value("\${app.kafka.events.topic}")
    private val topic: String,
    private val objectMapper: ObjectMapper,
    private val kafkaTemplate: KafkaTemplate<String, String>,
) : EventPublisher {
    override fun publish(event: Event) {
        try {
            val producerRecord = event.toProducerRecord()
            val future = kafkaTemplate.send(producerRecord)
            future.whenComplete { sendResult, ex ->
                if (ex != null) {
                    logger.error(ex) { "Error while publishing $sendResult" }
                } else {
                    logger.info { "Message sent successfully $sendResult" }
                }
            }
        } catch (ex: Exception) {
            logger.error(ex) { "Error while publishing $event" }
            throw ex
        }
    }

    override fun publishAll(events: List<Event>) {
        events.forEach(::publish)
    }

    fun Event.toProducerRecord(): ProducerRecord<String, String> {
        val payload = objectMapper.writeValueAsString(this)
        logger.info("Sending message to topic '$topic': $payload")
        val headers = listOf(createHeader(EVENT_TYPE_KEY, typeName()))
        return ProducerRecord(topic, null, eventId, payload, headers)
    }

    fun createHeader(
        key: String,
        value: String,
    ): Header = RecordHeader(key, value.toByteArray())

    private companion object : Logging
}
