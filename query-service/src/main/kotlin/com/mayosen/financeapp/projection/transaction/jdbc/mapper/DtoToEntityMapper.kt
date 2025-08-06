package com.mayosen.financeapp.projection.transaction.jdbc.mapper

import com.mayosen.financeapp.projection.transaction.Transaction
import com.mayosen.financeapp.projection.transaction.TransactionType
import com.mayosen.financeapp.projection.transaction.jdbc.TransactionEntity

object DtoToEntityMapper {
    fun Transaction.toEntity(): TransactionEntity =
        TransactionEntity(
            transactionId = transactionId,
            accountId = accountId,
            sourceEventId = sourceEventId,
            type = type.toTransactionEntityType(),
            amount = amount,
            timestamp = timestamp,
            relatedAccountId = relatedAccountId,
            isNewEntity = true,
        )

    private fun TransactionType.toTransactionEntityType(): TransactionEntity.TransactionType =
        when (this) {
            TransactionType.DEPOSIT -> TransactionEntity.TransactionType.DEPOSIT
            TransactionType.WITHDRAWAL -> TransactionEntity.TransactionType.WITHDRAWAL
            TransactionType.TRANSFER_IN -> TransactionEntity.TransactionType.TRANSFER_IN
            TransactionType.TRANSFER_OUT -> TransactionEntity.TransactionType.TRANSFER_OUT
        }
}
