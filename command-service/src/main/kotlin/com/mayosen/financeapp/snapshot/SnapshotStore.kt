package com.mayosen.financeapp.snapshot

import com.mayosen.financeapp.aggregate.AccountAggregate

/**
 * Оптимизирует восстановления агрегатов.
 */
interface SnapshotStore {
    fun findByAggregateId(aggregateId: String): AccountSnapshot?

    fun save(aggregate: AccountAggregate)
}
