package com.mayosen.financeapp.query

import com.mayosen.financeapp.query.api.GetAccountSummaryQuery
import com.mayosen.financeapp.query.api.GetAccountSummaryResponse
import com.mayosen.financeapp.query.api.GetTransactionHistoryQuery
import com.mayosen.financeapp.query.api.GetTransactionHistoryQuery.Pagination
import com.mayosen.financeapp.query.api.GetTransactionHistoryQuery.TimePeriod
import com.mayosen.financeapp.query.api.GetTransactionHistoryResponse
import com.mayosen.financeapp.query.api.ListAccountsQuery
import com.mayosen.financeapp.query.api.ListAccountsResponse
import com.mayosen.financeapp.query.api.QueryGateway
import com.mayosen.financeapp.query.api.v1.Account
import com.mayosen.financeapp.query.api.v1.QueryApi
import com.mayosen.financeapp.query.api.v1.Transaction
import com.mayosen.financeapp.query.api.v1.TransactionType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import java.time.Instant
import com.mayosen.financeapp.query.api.v1.GetAccountSummaryResponse as GetAccountSummaryApiResponse
import com.mayosen.financeapp.query.api.v1.GetTransactionHistoryResponse as GetTransactionHistoryApiResponse
import com.mayosen.financeapp.query.api.v1.ListAccountsResponse as ListAccountsApiResponse

@RestController
class QueryController(
    private val queryGateway: QueryGateway,
) : QueryApi {
    override fun listAccounts(ownerId: String): ResponseEntity<ListAccountsApiResponse> {
        val query = ListAccountsQuery(ownerId)
        val queryResponse: ListAccountsResponse = queryGateway.query(query)
        val apiResponse = queryResponse.toApiResponse()
        return ResponseEntity.ok(apiResponse)
    }

    private fun ListAccountsResponse.toApiResponse(): ListAccountsApiResponse {
        val accounts =
            accounts.map {
                Account(
                    accountId = it.accountId,
                    balance = it.balance,
                    updatedAt = it.updatedAt,
                )
            }
        return ListAccountsApiResponse(accounts)
    }

    override fun getAccountSummary(accountId: String): ResponseEntity<GetAccountSummaryApiResponse> {
        val query = GetAccountSummaryQuery(accountId)
        val queryResponse: GetAccountSummaryResponse = queryGateway.query(query)
        val apiResponse = queryResponse.toApiResponse()
        return ResponseEntity.ok(apiResponse)
    }

    private fun GetAccountSummaryResponse.toApiResponse() =
        GetAccountSummaryApiResponse(
            accountId = accountId,
            balance = balance,
            ownerId = ownerId,
            updatedAt = updatedAt,
        )

    override fun getTransactions(
        accountId: String,
        timeFrom: Instant?,
        timeTo: Instant?,
        pageOffset: Int,
        pageLimit: Int,
    ): ResponseEntity<GetTransactionHistoryApiResponse> {
        val timePeriod: TimePeriod? =
            if (timeFrom != null || timeTo != null) {
                TimePeriod(
                    from = timeFrom,
                    to = timeTo,
                )
            } else {
                null
            }
        val pagination = Pagination(offset = pageOffset, limit = pageLimit)
        val query =
            GetTransactionHistoryQuery(
                accountId = accountId,
                timePeriod = timePeriod,
                pagination = pagination,
            )
        val queryResponse: GetTransactionHistoryResponse = queryGateway.query(query)
        val apiResponse = queryResponse.toApiResponse()
        return ResponseEntity.ok(apiResponse)
    }

    private fun GetTransactionHistoryResponse.toApiResponse(): GetTransactionHistoryApiResponse {
        val transactions = transactions.map { it.toTransaction() }
        return GetTransactionHistoryApiResponse(
            transactions = transactions,
            pagination = pagination.toPagination(),
        )
    }

    private fun GetTransactionHistoryResponse.Transaction.toTransaction(): Transaction =
        Transaction(
            transactionId = transactionId,
            // TODO: Map explicitly
            type = TransactionType.valueOf(type.name),
            amount = amount,
            timestamp = timestamp,
            relatedAccountId = relatedAccountId,
        )

    private fun GetTransactionHistoryResponse.Pagination.toPagination() =
        com.mayosen.financeapp.query.api.v1.Pagination(
            hasMore = hasMore,
            total = total,
        )

    private companion object {
        const val DEFAULT_PAGE_OFFSET = 0
        const val DEFAULT_PAGE_LIMIT = 20
    }
}
