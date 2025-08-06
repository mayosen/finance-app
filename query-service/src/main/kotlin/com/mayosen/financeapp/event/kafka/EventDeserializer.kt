package com.mayosen.financeapp.event.kafka

import com.fasterxml.jackson.databind.ObjectMapper
import com.mayosen.financeapp.event.AccountCreatedEvent
import com.mayosen.financeapp.event.DepositPerformedEvent
import com.mayosen.financeapp.event.Event
import com.mayosen.financeapp.event.ResetReadModelEvent
import com.mayosen.financeapp.event.TransferPerformedEvent
import com.mayosen.financeapp.event.WithdrawalPerformedEvent
import org.apache.logging.log4j.kotlin.Logging
import org.springframework.stereotype.Component

@Component
class EventDeserializer(
    private val objectMapper: ObjectMapper,
) {
    fun deserialize(message: String): Event {
        val jsonNode = objectMapper.readTree(message)
        val eventType =
            jsonNode["type"]
                ?.asText()
                ?: throw IllegalArgumentException("Missing event type")

        val eventClass =
            EVENT_MAP[eventType]
                ?: throw IllegalArgumentException("Unknown event type: $eventType")

        return objectMapper.treeToValue(jsonNode["event"], eventClass) as Event
    }

    private companion object : Logging {
        val EVENT_MAP: Map<String, Class<*>> =
            listOf(
                AccountCreatedEvent::class,
                TransferPerformedEvent::class,
                DepositPerformedEvent::class,
                WithdrawalPerformedEvent::class,
                ResetReadModelEvent::class,
            ).associate {
                it.qualifiedName!! to it.java
            }
    }
}
