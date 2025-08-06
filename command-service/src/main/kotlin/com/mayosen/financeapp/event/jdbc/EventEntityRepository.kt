package com.mayosen.financeapp.event.jdbc

import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface EventEntityRepository : CrudRepository<EventEntity, String> {
    fun countByAggregateId(aggregateId: String): Int

    fun findAllByAggregateId(aggregateId: String): List<EventEntity>

    fun findAllByAggregateIdAndSequenceNumberGreaterThan(
        aggregateId: String,
        sequenceNumber: Long,
    ): List<EventEntity>

    @Query("SELECT MAX(sequence_number) FROM event WHERE aggregate_id = :aggregateId ")
    fun findMaxSequenceNumberByAggregateId(aggregateId: String): Long?
}
