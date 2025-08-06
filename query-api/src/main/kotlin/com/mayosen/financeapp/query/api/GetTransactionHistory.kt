package com.mayosen.financeapp.query.api

import com.mayosen.financeapp.query.Query
import java.math.BigDecimal
import java.time.Instant

class GetTransactionHistoryQuery(
    val accountId: String,
    val timePeriod: TimePeriod?,
    val pagination: Pagination,
) : Query {
    class TimePeriod(
        val from: Instant? = null,
        val to: Instant? = null,
    )

    class Pagination(
        val offset: Int = 0,
        val limit: Int = 50,
    )
}

class GetTransactionHistoryResponse(
    val transactions: List<Transaction>,
    val pagination: Pagination,
) {
    class Transaction(
        val transactionId: String,
        val type: TransactionType,
        val amount: BigDecimal,
        val timestamp: Instant,
        val relatedAccountId: String?,
    )

    enum class TransactionType {
        DEPOSIT,
        WITHDRAWAL,
        TRANSFER_OUT,
        TRANSFER_IN,
    }

    class Pagination(
        val hasMore: Boolean,
        val total: Int,
    )
}
