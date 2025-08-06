package com.mayosen.financeapp.query.api

import com.mayosen.financeapp.query.Query
import java.math.BigDecimal
import java.time.Instant

class GetAccountSummaryQuery(
    val accountId: String,
) : Query

class GetAccountSummaryResponse(
    val accountId: String,
    val balance: BigDecimal,
    val ownerId: String,
    val updatedAt: Instant,
)
