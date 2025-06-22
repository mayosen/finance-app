package com.mayosen.financeapp.projection.transaction

import java.math.BigDecimal
import java.time.Instant

/**
 * 	Проекция для истории операций.
 */
class TransactionHistory(
    val transactions: List<Transaction>,
    val pagination: Pagination,
) {
    class Pagination(
        val hasMore: Boolean,
        val total: Int,
    )
}

class Transaction(
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
