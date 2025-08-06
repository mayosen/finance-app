package com.mayosen.financeapp.snapshot

import com.mayosen.financeapp.aggregate.AccountAggregate

/**
 * Оптимизирует восстановления агрегатов.
 */
interface SnapshotStore {
    fun findByAccountId(accountId: String): AccountSnapshot?

    fun save(aggregate: AccountAggregate)

    fun delete(aggregate: AccountAggregate)
}
