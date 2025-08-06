package com.mayosen.financeapp.command.api

import com.mayosen.financeapp.command.Command
import java.math.BigDecimal

class CreateAccountCommand(
    override val id: String,
    val ownerId: String,
) : Command

class TransferCommand(
    override val id: String,
    val fromAccountId: String,
    val toAccountId: String,
    val amount: BigDecimal,
) : Command

class DepositCommand(
    override val id: String,
    val accountId: String,
    val amount: BigDecimal,
) : Command

class WithdrawCommand(
    override val id: String,
    val accountId: String,
    val amount: BigDecimal,
) : Command
