package com.mayosen.financeapp.test.generator

import com.mayosen.financeapp.command.api.v1.CreateAccountRequest
import com.mayosen.financeapp.command.api.v1.DepositRequest
import com.mayosen.financeapp.test.ACCOUNT_ID
import com.mayosen.financeapp.test.AMOUNT_100
import com.mayosen.financeapp.test.OWNER_ID

fun generateCreateAccountRequest(): CreateAccountRequest = CreateAccountRequest(ownerId = OWNER_ID)

fun generateDepositRequest(): DepositRequest = DepositRequest(accountId = ACCOUNT_ID, amount = AMOUNT_100)
