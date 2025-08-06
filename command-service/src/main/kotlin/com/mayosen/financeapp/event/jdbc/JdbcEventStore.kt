package com.mayosen.financeapp.event.jdbc

import com.mayosen.financeapp.event.Event
import com.mayosen.financeapp.event.EventStore
import org.springframework.stereotype.Service

@Service
class JdbcEventStore(
    private val eventEntityRepository: EventEntityRepository,
    private val eventSerializer: EventSerializer,
    private val eventDeserializer: EventDeserializer,
) : EventStore {
    override fun countByAggregateId(aggregateId: String): Int = eventEntityRepository.countByAggregateId(aggregateId)

    override fun findAllByAggregateId(aggregateId: String): List<Event> {
        val entities = eventEntityRepository.findAllByAggregateId(aggregateId)
        return entities.map { eventDeserializer.deserialize(it) }
    }

    override fun findAllByAggregateIdAfterSequenceNumber(
        aggregateId: String,
        sequenceNumber: Long,
    ): List<Event> {
        val entities =
            eventEntityRepository.findAllByAggregateIdAndSequenceNumberGreaterThan(aggregateId, sequenceNumber)
        return entities.map { eventDeserializer.deserialize(it) }
    }

    override fun save(event: Event) {
        val maxSequenceNumber =
            eventEntityRepository.findMaxSequenceNumberByAggregateId(event.aggregateId)
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
