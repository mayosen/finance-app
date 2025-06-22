package com.mayosen.financeapp.event.kafka

import com.mayosen.financeapp.config.KafkaConfiguration.Companion.LISTENER_CONTAINER_FACTORY
import com.mayosen.financeapp.event.Event
import com.mayosen.financeapp.event.EventHandler
import com.mayosen.financeapp.event.EventSubscriber
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.logging.log4j.kotlin.Logging
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Service
import kotlin.properties.Delegates

@Service
class KafkaEventSubscriber : EventSubscriber {
    private var eventHandler: EventHandler by Delegates.notNull()

    override fun subscribe(handler: EventHandler) {
        eventHandler = handler
    }

    // TODO: Use properties
    @KafkaListener(
        topics = ["\${app.kafka.events.topic}"],
        groupId = "query-service",
        // idIsGroup = false,
        containerFactory = LISTENER_CONTAINER_FACTORY,
    )
    fun onMessage(
        record: ConsumerRecord<String, Event>,
        acknowledgment: Acknowledgment,
    ) {
        try {
            logger.info("Got message: ${record.value()}")
            eventHandler.handle(record.value())
            acknowledgment.acknowledge()
            logger.info("Message was processed")
        } catch (ex: Exception) {
            logger.error("Failed to deserialize and process event: ${ex.message}", ex)
        }
    }

    private companion object : Logging
}
