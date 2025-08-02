package com.mayosen.financeapp.query.mapper

import com.mayosen.financeapp.query.api.GetAccountSummaryResponse

fun GetAccountSummaryResponse.toResponse(): com.mayosen.financeapp.query.api.v1.GetAccountSummaryResponse =
    com.mayosen.financeapp.query.api.v1.GetAccountSummaryResponse(
        accountId = accountId,
        balance = balance,
        ownerId = ownerId,
        updatedAt = updatedAt,
    )
