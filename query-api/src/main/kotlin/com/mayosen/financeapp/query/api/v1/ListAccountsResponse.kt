package com.mayosen.financeapp.query.api.v1

import java.math.BigDecimal
import java.time.Instant

data class ListAccountsResponse(
    val accounts: List<Account>,
) {
    data class Account(
        val accountId: String,
        val balance: BigDecimal,
        val updatedAt: Instant,
    )
}

