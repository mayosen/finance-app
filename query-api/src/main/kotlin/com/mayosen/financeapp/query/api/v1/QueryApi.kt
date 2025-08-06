package com.mayosen.financeapp.query.api.v1

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import java.time.Instant

/**
 * REST API для запросов.
 */
@RequestMapping("/api/v1/queries")
interface QueryApi {
    @GetMapping("/accounts")
    fun listAccounts(
        @RequestParam(required = true) ownerId: String,
    ): ResponseEntity<ListAccountsResponse>

    @GetMapping("/accounts/{accountId}")
    fun getAccount(
        @PathVariable accountId: String,
    ): ResponseEntity<GetAccountSummaryResponse>

    @GetMapping("/accounts/{accountId}/transactions")
    fun getTransactions(
        @PathVariable accountId: String,
        @RequestParam(required = false) timeFrom: Instant?,
        @RequestParam(required = false) timeTo: Instant?,
        @RequestParam(required = false, defaultValue = "0") pageOffset: Int?,
        @RequestParam(required = false, defaultValue = "20") pageLimit: Int?,
    ): ResponseEntity<GetTransactionHistoryResponse>
}
