package com.mayosen.financeapp.query.mapper

import com.mayosen.financeapp.query.api.ListAccountsResponse
import com.mayosen.financeapp.query.api.v1.Account

fun ListAccountsResponse.toResponse(): com.mayosen.financeapp.query.api.v1.ListAccountsResponse {
    val accounts =
        accounts.map {
            Account(
                accountId = it.accountId,
                balance = it.balance,
                updatedAt = it.updatedAt,
            )
        }
    return com.mayosen.financeapp.query.api.v1
        .ListAccountsResponse(accounts)
}
