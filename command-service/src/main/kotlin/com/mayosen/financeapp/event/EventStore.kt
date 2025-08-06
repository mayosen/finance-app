package com.mayosen.financeapp.event

/**
 * Хранилище событий
 */
interface EventStore {
    fun countByAggregateId(aggregateId: String): Int

    fun findAllByAggregateId(aggregateId: String): List<Event>

    fun findAllByAggregateIdAfterSequenceNumber(
        aggregateId: String,
        sequenceNumber: Long,
    ): List<Event>

    fun save(event: Event)

    fun saveAll(events: List<Event>)
}
