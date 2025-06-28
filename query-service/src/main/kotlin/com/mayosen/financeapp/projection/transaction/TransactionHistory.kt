package com.mayosen.financeapp.projection.transaction

import java.math.BigDecimal
import java.time.Instant

/**
 * 	Проекция для истории операций.
 */
data class TransactionHistory(
    val transactions: List<Transaction>,
    val pagination: Pagination,
) {
    data class Pagination(
        val hasMore: Boolean,
        val total: Int,
    )
}

data class Transaction(
    val accountId: String,
    val transactionId: String,
    val sourceEventId: String,
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
