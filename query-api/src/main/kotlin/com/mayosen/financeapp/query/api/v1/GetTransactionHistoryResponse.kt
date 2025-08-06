package com.mayosen.financeapp.query.api.v1

import java.math.BigDecimal
import java.time.Instant

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
