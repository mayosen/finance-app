package com.mayosen.financeapp.event.jdbc

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import com.mayosen.financeapp.event.Event
import org.springframework.stereotype.Component

@Component
class EventSerializer(
    private val objectMapper: ObjectMapper,
) {
    fun serialize(
        event: Event,
        sequenceNumber: Long,
        isNew: Boolean,
    ): EventEntity {
        val tree = objectMapper.valueToTree<ObjectNode>(event)
        EXPLICIT_FIELDS.forEach { tree.remove(it) }

        // TODO: or tree.toString()
        val eventFields = objectMapper.writeValueAsString(tree)
        return EventEntity(
            eventId = event.eventId,
            sequenceNumber = sequenceNumber,
            accountId = event.accountId,
            eventType = event::class.qualifiedName!!,
            eventFields = eventFields,
            timestamp = event.timestamp,
            isNewEntity = isNew,
        )
    }
}
