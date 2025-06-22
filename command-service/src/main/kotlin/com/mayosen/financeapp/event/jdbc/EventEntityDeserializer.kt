package com.mayosen.financeapp.event.jdbc

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import com.mayosen.financeapp.event.AccountCreatedEvent
import com.mayosen.financeapp.event.DepositPerformedEvent
import com.mayosen.financeapp.event.Event
import com.mayosen.financeapp.event.TransferPerformedEvent
import com.mayosen.financeapp.event.WithdrawalPerformedEvent
import org.springframework.stereotype.Component
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

@Component
class EventEntityDeserializer(
    private val objectMapper: ObjectMapper,
) {
    fun deserialize(entity: EventEntity): Event {
        val kClass = Class.forName(entity.eventType).kotlin

        if (!kClass.isSubclassOf(Event::class)) {
            throw IllegalArgumentException()
        }

        val event: Event =
            when {
                kClass.isSubclassOf(AccountCreatedEvent::class) -> {
                    parseAndCast(entity, AccountCreatedEvent::class)
                }

                kClass.isSubclassOf(TransferPerformedEvent::class) -> {
                    parseAndCast(entity, TransferPerformedEvent::class)
                }

                kClass.isSubclassOf(DepositPerformedEvent::class) -> {
                    parseAndCast(entity, DepositPerformedEvent::class)
                }

                kClass.isSubclassOf(WithdrawalPerformedEvent::class) -> {
                    parseAndCast(entity, WithdrawalPerformedEvent::class)
                }

                else -> throw IllegalArgumentException()
            }

        return event
    }

    private fun <T : Any> parseAndCast(
        event: EventEntity,
        eventType: KClass<T>,
    ): T {
        val tree = objectMapper.readTree(event.eventFields) as ObjectNode
        // TODO: Сделать так, чтобы при добавлении нового поля тут не забыть проставить
        tree.put(EVENT_ID_FIELD, event.eventId)
        tree.put(ACCOUNT_ID_FIELD, event.accountId)
        tree.put(TIMESTAMP_ID_FIELD, event.timestamp.toEpochMilli())
        return objectMapper.treeToValue(tree, eventType.java)
    }
}
