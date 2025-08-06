package com.mayosen.financeapp.query.api

import com.mayosen.financeapp.query.Query
import java.math.BigDecimal
import java.time.Instant

data class ListAccountsQuery(
    val ownerId: String,
) : Query

data class ListAccountsResponse(
    val accounts: List<Account>,
) {
    data class Account(
        val accountId: String,
        val balance: BigDecimal,
        val updatedAt: Instant,
    )
}
