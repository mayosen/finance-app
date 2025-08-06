package com.mayosen.financeapp.readmodel.transactionhistory.jdbc

import java.time.Instant

interface TransactionViewEntityCustomRepository {
    fun findByFilters(
        accountId: String,
        from: Instant?,
        to: Instant?,
        offset: Int,
        limit: Int,
    ): List<TransactionViewEntity>

    fun countByFilters(
        accountId: String,
        from: Instant?,
        to: Instant?,
    ): Int
}
