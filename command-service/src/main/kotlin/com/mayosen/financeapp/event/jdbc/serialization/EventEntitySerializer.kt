package com.mayosen.financeapp.event.jdbc.serialization

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import com.mayosen.financeapp.event.Event
import com.mayosen.financeapp.event.jdbc.EventEntity
import com.mayosen.financeapp.event.jdbc.EventFields
import com.mayosen.financeapp.event.jdbc.serialization.rules.ExplicitFieldRule
import com.mayosen.financeapp.event.jdbc.serialization.rules.RulesProvider
import com.mayosen.financeapp.event.serialization.typeName
import org.springframework.stereotype.Component

@Component
class EventEntitySerializer(
    private val objectMapper: ObjectMapper,
    rulesProvider: RulesProvider,
) {
    private val explicitFields: List<ExplicitFieldRule<*>> = rulesProvider.getRules()

    fun serialize(
        event: Event,
        sequenceNumber: Long,
        isNew: Boolean,
    ): EventEntity {
        val eventFields = event.buildEventFields()
        return EventEntity(
            eventId = event.eventId,
            sequenceNumber = sequenceNumber,
            accountId = event.accountId,
            eventType = event.typeName(),
            eventFields = eventFields,
            timestamp = event.timestamp,
            isNewEntity = isNew,
        )
    }

    private fun Event.buildEventFields(): EventFields {
        val tree = objectMapper.valueToTree<ObjectNode>(this)
        explicitFields.forEach { it.removeFrom(tree) }
        return EventFields(tree)
    }
}
