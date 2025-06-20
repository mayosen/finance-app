package com.mayosen.financeapp.snapshot

import com.mayosen.financeapp.aggregate.AccountAggregate

interface CreateSnapshotStrategy {
    fun shouldCreateSnapshot(aggregate: AccountAggregate): Boolean
}
