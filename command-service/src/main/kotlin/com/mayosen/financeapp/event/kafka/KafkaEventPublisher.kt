package com.mayosen.financeapp.event.kafka

import com.fasterxml.jackson.databind.ObjectMapper
import com.mayosen.financeapp.event.Event
import com.mayosen.financeapp.event.EventPublisher
import org.apache.logging.log4j.kotlin.Logging
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class KafkaEventPublisher(
    @Value("\${app.kafka.events.topic}")
    private val topic: String,
    private val objectMapper: ObjectMapper,
    private val kafkaTemplate: KafkaTemplate<String, String>,
) : EventPublisher,
    InitializingBean,
    Logging {
    override fun publish(event: Event) {
        val dto = EventDto(type = event::class.qualifiedName!!, event = event)
        val json = objectMapper.writeValueAsString(dto)
        try {
            logger.info("Sending message to topic '$topic': $json")
            val future = kafkaTemplate.send(topic, event.aggregateId, json)
            future.whenComplete { sendResult, ex ->
                if (ex != null) {
                    logger.error(ex) { "Error while publishing $sendResult" }
                } else {
                    logger.info { "Message sent $sendResult" }
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

    override fun afterPropertiesSet() {
        // TODO: Хотя здесь уже по умолчанию стоит 5s, очень странно
        // https://stackoverflow.com/a/49494338/18989230
        // kafkaTemplate.setCloseTimeout(Duration.ofSeconds(5))
    }
}
