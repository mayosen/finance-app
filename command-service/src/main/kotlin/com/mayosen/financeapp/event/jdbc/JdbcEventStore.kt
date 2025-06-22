package com.mayosen.financeapp.event.jdbc

import com.mayosen.financeapp.event.Event
import com.mayosen.financeapp.event.EventStore
import com.mayosen.financeapp.event.typeName
import org.springframework.stereotype.Service
import kotlin.reflect.KClass

@Service
class JdbcEventStore(
    private val eventEntityRepository: EventEntityRepository,
    private val eventSerializer: EventEntitySerializer,
    private val eventDeserializer: EventEntityDeserializer,
) : EventStore {
    override fun countByAccountId(accountId: String): Int = eventEntityRepository.countByAccountId(accountId)

    override fun findAllByAccountId(accountId: String): List<Event> {
        val entities = eventEntityRepository.findAllByAccountId(accountId)
        return entities.map { eventDeserializer.deserialize(it) }
    }

    override fun findAllByAccountIdAfterSequenceNumber(
        accountId: String,
        sequenceNumber: Long,
    ): List<Event> {
        val entities =
            eventEntityRepository.findAllByAccountIdAndSequenceNumberGreaterThan(accountId, sequenceNumber)
        return entities.map { eventDeserializer.deserialize(it) }
    }

    override fun findAllByAccountIdAndTypeIn(
        accountId: String,
        types: List<KClass<out Event>>,
    ): List<Event> {
        val eventTypes = types.map { it.typeName() }
        val entities = eventEntityRepository.findAllByAccountIdAndEventTypeIn(accountId, eventTypes)
        return entities.map { eventDeserializer.deserialize(it) }
    }

    override fun save(event: Event) {
        val maxSequenceNumber =
            eventEntityRepository.findMaxSequenceNumberByAccountId(event.accountId)
                ?: 0
        val entity =
            eventSerializer
                .serialize(
                    event = event,
                    sequenceNumber = maxSequenceNumber + 1,
                    isNew = true,
                )
        eventEntityRepository.save(entity)
    }

    override fun saveAll(events: List<Event>) {
        events.forEach(::save)
    }
}
