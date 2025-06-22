package com.mayosen.financeapp.test.generator

import com.mayosen.financeapp.query.api.v1.GetAccountSummaryResponse
import com.mayosen.financeapp.test.ACCOUNT_ID
import com.mayosen.financeapp.test.BALANCE_50
import com.mayosen.financeapp.test.INSTANT
import com.mayosen.financeapp.test.OWNER_ID

fun generateGetAccountSummaryResponse(): GetAccountSummaryResponse =
    GetAccountSummaryResponse(
        accountId = ACCOUNT_ID,
        balance = BALANCE_50,
        ownerId = OWNER_ID,
        updatedAt = INSTANT,
    )
