package com.mayosen.financeapp.test.generator

import com.mayosen.financeapp.query.api.v1.Account
import com.mayosen.financeapp.query.api.v1.GetAccountSummaryResponse
import com.mayosen.financeapp.query.api.v1.GetTransactionHistoryResponse
import com.mayosen.financeapp.query.api.v1.ListAccountsResponse
import com.mayosen.financeapp.query.api.v1.Pagination
import com.mayosen.financeapp.query.api.v1.Transaction
import com.mayosen.financeapp.query.api.v1.TransactionType
import com.mayosen.financeapp.test.ACCOUNT_ID
import com.mayosen.financeapp.test.AMOUNT_50
import com.mayosen.financeapp.test.INSTANT
import com.mayosen.financeapp.test.OWNER_ID
import com.mayosen.financeapp.test.TRANSACTION_ID
import java.math.BigDecimal
import java.time.Instant

fun generateGetAccountSummaryResponse(): GetAccountSummaryResponse =
    GetAccountSummaryResponse(
        accountId = ACCOUNT_ID,
        balance = AMOUNT_50,
        ownerId = OWNER_ID,
        updatedAt = INSTANT,
    )

fun generateGetTransactionHistoryResponse(transactions: List<Transaction> = listOf(generateTransaction())): GetTransactionHistoryResponse =
    GetTransactionHistoryResponse(
        transactions = transactions,
        pagination = generatePagination(),
    )

fun generateTransaction(
    transactionId: String = TRANSACTION_ID,
    transactionType: TransactionType = TransactionType.DEPOSIT,
    amount: BigDecimal = AMOUNT_50,
    timestamp: Instant = INSTANT,
): Transaction =
    Transaction(
        transactionId = transactionId,
        type = transactionType,
        amount = amount,
        timestamp = timestamp,
        relatedAccountId = null,
    )

fun generatePagination(): Pagination =
    Pagination(
        hasMore = false,
        total = 1,
    )

fun generateListAccountsResponse(accounts: List<Account> = listOf(generateAccount())): ListAccountsResponse =
    ListAccountsResponse(accounts = accounts)

fun generateAccount(
    accountId: String = ACCOUNT_ID,
    balance: BigDecimal = AMOUNT_50,
    updatedAt: Instant = INSTANT,
): Account =
    Account(
        accountId = accountId,
        balance = balance,
        updatedAt = updatedAt,
    )
