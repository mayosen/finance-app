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
import com.mayosen.financeapp.query.api.v1.QueryApi
import com.mayosen.financeapp.query.mapper.toResponse
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
        val result: ListAccountsResponse = queryGateway.query(query)
        return ResponseEntity.ok(result.toResponse())
    }

    override fun getAccountSummary(accountId: String): ResponseEntity<GetAccountSummaryApiResponse> {
        val query = GetAccountSummaryQuery(accountId)
        val result: GetAccountSummaryResponse = queryGateway.query(query)
        return ResponseEntity.ok(result.toResponse())
    }

    override fun getTransactions(
        accountId: String,
        timeFrom: Instant?,
        timeTo: Instant?,
        pageOffset: Int,
        pageLimit: Int,
    ): ResponseEntity<GetTransactionHistoryApiResponse> {
        val timePeriod: TimePeriod? =
            if (timeFrom != null || timeTo != null) {
                TimePeriod(from = timeFrom, to = timeTo)
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
        val result: GetTransactionHistoryResponse = queryGateway.query(query)
        return ResponseEntity.ok(result.toResponse())
    }
}
