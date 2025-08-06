package com.mayosen.financeapp.query

import com.mayosen.financeapp.query.api.GetAccountSummaryQuery
import com.mayosen.financeapp.query.api.GetTransactionHistoryQuery
import com.mayosen.financeapp.query.api.ListAccountsQuery
import com.mayosen.financeapp.query.api.QueryGateway
import org.springframework.stereotype.Component

@Component
class SyncQueryGateway(
    private val queryHandler: QueryHandler,
) : QueryGateway {
    @Suppress("UNCHECKED_CAST")
    override fun <T : Query, R : Any> query(query: T): R =
        when (query) {
            is ListAccountsQuery -> queryHandler.handleListAccounts(query) as R
            is GetAccountSummaryQuery -> queryHandler.handleGetAccountSummary(query) as R
            is GetTransactionHistoryQuery -> queryHandler.handleGetTransactionHistory(query) as R
            else -> throw UnsupportedOperationException("Unsupported query type: ${query::class}")
        }
}
