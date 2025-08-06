package com.mayosen.financeapp.query

import com.mayosen.financeapp.query.api.GetAccountSummaryQuery
import com.mayosen.financeapp.query.api.GetAccountSummaryResponse
import com.mayosen.financeapp.query.api.GetTransactionHistoryQuery
import com.mayosen.financeapp.query.api.GetTransactionHistoryResponse
import com.mayosen.financeapp.query.api.ListAccountsQuery
import com.mayosen.financeapp.query.api.ListAccountsResponse
import com.mayosen.financeapp.readmodel.accountsummary.AccountSummaryStore
import com.mayosen.financeapp.readmodel.transactionhistory.Pagination
import com.mayosen.financeapp.readmodel.transactionhistory.TimePeriod
import com.mayosen.financeapp.readmodel.transactionhistory.Transaction
import com.mayosen.financeapp.readmodel.transactionhistory.TransactionHistory
import com.mayosen.financeapp.readmodel.transactionhistory.TransactionHistoryStore
import org.springframework.stereotype.Service

@Service
class QueryHandler(
    private val accountSummaryStore: AccountSummaryStore,
    private val transactionHistoryStore: TransactionHistoryStore,
) {
    fun handleListAccounts(query: ListAccountsQuery): ListAccountsResponse {
        val accounts =
            accountSummaryStore
                .findAllByOwnerId(query.ownerId)
                .map {
                    ListAccountsResponse.Account(
                        accountId = it.accountId,
                        balance = it.balance,
                        updatedAt = it.updatedAt,
                    )
                }
        return ListAccountsResponse(accounts)
    }

    fun handleGetAccountSummary(query: GetAccountSummaryQuery): GetAccountSummaryResponse {
        val account =
            accountSummaryStore.findByAccountId(query.accountId)
                ?: throw IllegalArgumentException("Account with id ${query.accountId} not found")
        return GetAccountSummaryResponse(
            accountId = account.accountId,
            balance = account.balance,
            ownerId = account.ownerId,
            updatedAt = account.updatedAt,
        )
    }

    fun handleGetTransactionHistory(query: GetTransactionHistoryQuery): GetTransactionHistoryResponse {
        val timePeriod =
            TimePeriod(
                from = query.timePeriod?.from,
                to = query.timePeriod?.to,
            )
        val pagination =
            Pagination(
                offset = query.pagination.offset,
                limit = query.pagination.limit,
            )
        val transactionHistory =
            transactionHistoryStore.findByAccountId(
                accountId = query.accountId,
                timePeriod = timePeriod,
                pagination = pagination,
            )

        return toResponse(transactionHistory)
    }

    private fun toResponse(transactionHistory: TransactionHistory): GetTransactionHistoryResponse {
        val transactions = transactionHistory.transactions.map { it.toTransaction() }
        return GetTransactionHistoryResponse(
            transactions = transactions,
            pagination = transactionHistory.pagination.toPagination(),
        )
    }

    private fun Transaction.toTransaction(): GetTransactionHistoryResponse.Transaction =
        GetTransactionHistoryResponse.Transaction(
            transactionId = transactionId,
            // TODO: Map explicitly
            type = GetTransactionHistoryResponse.TransactionType.valueOf(type.name),
            amount = amount,
            timestamp = timestamp,
            relatedAccountId = relatedAccountId,
        )

    private fun TransactionHistory.Pagination.toPagination() =
        GetTransactionHistoryResponse.Pagination(
            hasMore = hasMore,
            total = total,
        )
}
