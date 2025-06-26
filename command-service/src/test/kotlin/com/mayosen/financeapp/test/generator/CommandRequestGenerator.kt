package com.mayosen.financeapp.test.generator

import com.mayosen.financeapp.command.api.v1.CreateAccountRequest
import com.mayosen.financeapp.command.api.v1.DeleteAccountRequest
import com.mayosen.financeapp.command.api.v1.DepositRequest
import com.mayosen.financeapp.command.api.v1.TransferRequest
import com.mayosen.financeapp.command.api.v1.WithdrawRequest
import com.mayosen.financeapp.test.ACCOUNT_ID
import com.mayosen.financeapp.test.ACCOUNT_ID_2
import com.mayosen.financeapp.test.AMOUNT_100
import com.mayosen.financeapp.test.AMOUNT_50
import com.mayosen.financeapp.test.OWNER_ID

fun generateCreateAccountRequest(): CreateAccountRequest = CreateAccountRequest(ownerId = OWNER_ID)

fun generateDepositRequest(): DepositRequest = DepositRequest(accountId = ACCOUNT_ID, amount = AMOUNT_100)

fun generateWithdrawRequest(): WithdrawRequest = WithdrawRequest(accountId = ACCOUNT_ID, amount = AMOUNT_50)

fun generateTransferRequest(): TransferRequest = TransferRequest(fromAccountId = ACCOUNT_ID, toAccountId = ACCOUNT_ID_2, amount = AMOUNT_50)

fun generateDeleteAccountRequest(): DeleteAccountRequest = DeleteAccountRequest(accountId = ACCOUNT_ID)
