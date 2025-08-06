package com.mayosen.financeapp.snapshot

interface CreateSnapshotStrategy {
    fun shouldCreateSnapshot(accountId: String): Boolean
}
