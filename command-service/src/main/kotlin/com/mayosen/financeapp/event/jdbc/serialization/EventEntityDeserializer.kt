package com.mayosen.financeapp.event.jdbc.serialization

import com.fasterxml.jackson.databind.ObjectMapper
import com.mayosen.financeapp.event.Event
import com.mayosen.financeapp.event.jdbc.EventEntity
import com.mayosen.financeapp.event.jdbc.serialization.rules.ExplicitFieldRule
import com.mayosen.financeapp.event.jdbc.serialization.rules.RulesProvider
import com.mayosen.financeapp.event.serialization.EventType
import org.springframework.stereotype.Component
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

@Component
class EventEntityDeserializer(
    private val objectMapper: ObjectMapper,
    rulesProvider: RulesProvider,
) {
    private val explicitFields: List<ExplicitFieldRule<*>> = rulesProvider.getRules()

    @Suppress("UNCHECKED_CAST")
    fun deserialize(entity: EventEntity): Event {
        val kClass = Class.forName(entity.eventType).kotlin
        if (!kClass.isSubclassOf(Event::class)) {
            throw IllegalArgumentException()
        }
        val eventClass = kClass as EventType
        return parseAndCast(entity, eventClass)
    }

    private fun <T : Event> parseAndCast(
        event: EventEntity,
        eventType: KClass<T>,
    ): T {
        val tree = event.eventFields.tree
        explicitFields.forEach {
            it.writeTo(event, tree)
        }
        return objectMapper.treeToValue(tree, eventType.java)
    }
}
