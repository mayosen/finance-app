package com.mayosen.financeapp.command.api.v1

import java.math.BigDecimal

class CreateAccountRequest(
    val ownerId: String,
)

class DepositRequest(
    val accountId: String,
    val amount: BigDecimal,
)

class WithdrawRequest(
    val accountId: String,
    val amount: BigDecimal,
)

class TransferRequest(
    val fromAccountId: String,
    val toAccountId: String,
    val amount: BigDecimal,
)
