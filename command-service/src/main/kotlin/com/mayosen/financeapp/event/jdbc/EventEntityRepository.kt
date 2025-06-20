package com.mayosen.financeapp.event.jdbc

import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface EventEntityRepository : CrudRepository<EventEntity, String> {
    fun countByAccountId(accountId: String): Int

    fun findAllByAccountId(accountId: String): List<EventEntity>

    fun findAllByAccountIdAndSequenceNumberGreaterThan(
        accountId: String,
        sequenceNumber: Long,
    ): List<EventEntity>

    fun findAllByAccountIdAndEventTypeIn(
        accountId: String,
        eventTypes: List<String>,
    ): List<EventEntity>

    @Query("SELECT MAX(sequence_number) FROM event WHERE account_id = :accountId ")
    fun findMaxSequenceNumberByAccountId(accountId: String): Long?
}
