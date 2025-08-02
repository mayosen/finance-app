package com.mayosen.financeapp.query.mapper

import com.mayosen.financeapp.query.api.GetTransactionHistoryResponse
import com.mayosen.financeapp.query.api.v1.Transaction
import com.mayosen.financeapp.query.api.v1.TransactionType

fun GetTransactionHistoryResponse.toResponse(): com.mayosen.financeapp.query.api.v1.GetTransactionHistoryResponse {
    val transactions = transactions.map { it.toTransaction() }
    return com.mayosen.financeapp.query.api.v1.GetTransactionHistoryResponse(
        transactions = transactions,
        pagination = pagination.toPagination(),
    )
}

private fun GetTransactionHistoryResponse.Transaction.toTransaction(): Transaction =
    Transaction(
        transactionId = transactionId,
        type = type.toType(),
        amount = amount,
        timestamp = timestamp,
        relatedAccountId = relatedAccountId,
    )

private fun GetTransactionHistoryResponse.TransactionType.toType(): TransactionType =
    when (this) {
        GetTransactionHistoryResponse.TransactionType.DEPOSIT -> TransactionType.DEPOSIT
        GetTransactionHistoryResponse.TransactionType.WITHDRAWAL -> TransactionType.WITHDRAWAL
        GetTransactionHistoryResponse.TransactionType.TRANSFER_IN -> TransactionType.TRANSFER_IN
        GetTransactionHistoryResponse.TransactionType.TRANSFER_OUT -> TransactionType.TRANSFER_OUT
    }

private fun GetTransactionHistoryResponse.Pagination.toPagination() =
    com.mayosen.financeapp.query.api.v1.Pagination(
        hasMore = hasMore,
        total = total,
    )
