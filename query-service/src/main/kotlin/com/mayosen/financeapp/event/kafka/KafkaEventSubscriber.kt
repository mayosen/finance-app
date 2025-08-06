package com.mayosen.financeapp.event.kafka

import com.mayosen.financeapp.event.EventHandler
import com.mayosen.financeapp.event.EventSubscriber
import org.apache.logging.log4j.kotlin.Logging
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Service
import kotlin.properties.Delegates

@Service
class KafkaEventSubscriber(
    private val eventDeserializer: EventDeserializer,
) : EventSubscriber {
    private var eventHandler: EventHandler by Delegates.notNull()

    override fun subscribe(handler: EventHandler) {
        eventHandler = handler
    }

    // TODO: Use properties
    @KafkaListener(topics = ["\${app.kafka.events.topic}"], groupId = "query-service")
    fun onMessage(
        message: String,
        acknowledgment: Acknowledgment,
    ) {
        try {
            logger.info("Got message: $message")
            val event = eventDeserializer.deserialize(message)
            eventHandler.handle(event)
            acknowledgment.acknowledge()
            logger.info("Message was processed")
        } catch (ex: Exception) {
            logger.error("Failed to deserialize and process event: ${ex.message}", ex)
        }
    }

    private companion object : Logging
}
