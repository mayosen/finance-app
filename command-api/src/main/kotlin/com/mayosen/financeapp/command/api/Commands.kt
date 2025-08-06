package com.mayosen.financeapp.command.api

import com.mayosen.financeapp.command.Command
import java.math.BigDecimal

data class CreateAccountCommand(
    override val id: String,
    val ownerId: String,
) : Command

data class TransferCommand(
    override val id: String,
    val fromAccountId: String,
    val toAccountId: String,
    val amount: BigDecimal,
) : Command

data class DepositCommand(
    override val id: String,
    val accountId: String,
    val amount: BigDecimal,
) : Command

data class WithdrawCommand(
    override val id: String,
    val accountId: String,
    val amount: BigDecimal,
) : Command

data class DeleteAccountCommand(
    override val id: String,
    val accountId: String,
) : Command
