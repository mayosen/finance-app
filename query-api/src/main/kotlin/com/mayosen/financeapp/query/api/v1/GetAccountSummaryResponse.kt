package com.mayosen.financeapp.query.api.v1

import java.math.BigDecimal
import java.time.Instant

class GetAccountSummaryResponse(
    val accountId: String,
    val balance: BigDecimal,
    val ownerId: String,
    val updatedAt: Instant,
)
