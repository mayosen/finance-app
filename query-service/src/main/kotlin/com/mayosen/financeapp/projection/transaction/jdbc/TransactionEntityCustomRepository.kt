package com.mayosen.financeapp.projection.transaction.jdbc

import java.time.Instant

interface TransactionEntityCustomRepository {
    fun findByFilters(
        accountId: String,
        from: Instant?,
        to: Instant?,
        offset: Int,
        limit: Int,
    ): List<TransactionEntity>

    fun countByFilters(
        accountId: String,
        from: Instant?,
        to: Instant?,
    ): Int
}
