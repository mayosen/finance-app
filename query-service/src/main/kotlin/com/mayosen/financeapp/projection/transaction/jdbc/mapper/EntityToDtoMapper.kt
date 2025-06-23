package com.mayosen.financeapp.projection.transaction.jdbc.mapper

import com.mayosen.financeapp.projection.transaction.Transaction
import com.mayosen.financeapp.projection.transaction.TransactionType
import com.mayosen.financeapp.projection.transaction.jdbc.TransactionEntity

object EntityToDtoMapper {
    fun TransactionEntity.toTransaction() =
        Transaction(
            accountId = accountId,
            transactionId = transactionId,
            sourceEventId = transactionId,
            type = type.toType(),
            amount = amount,
            timestamp = timestamp,
            relatedAccountId = relatedAccountId,
        )

    private fun TransactionEntity.TransactionType.toType(): TransactionType =
        when (this) {
            TransactionEntity.TransactionType.DEPOSIT -> TransactionType.DEPOSIT
            TransactionEntity.TransactionType.WITHDRAWAL -> TransactionType.WITHDRAWAL
            TransactionEntity.TransactionType.TRANSFER_IN -> TransactionType.TRANSFER_IN
            TransactionEntity.TransactionType.TRANSFER_OUT -> TransactionType.TRANSFER_OUT
        }
}
