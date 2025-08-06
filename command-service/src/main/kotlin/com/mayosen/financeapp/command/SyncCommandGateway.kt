package com.mayosen.financeapp.command

import com.mayosen.financeapp.admin.AdminCommandHandler
import com.mayosen.financeapp.command.api.AdminCommand
import com.mayosen.financeapp.command.api.CommandGateway
import com.mayosen.financeapp.command.api.CreateAccountCommand
import com.mayosen.financeapp.command.api.DepositCommand
import com.mayosen.financeapp.command.api.ReplayEventsCommand
import com.mayosen.financeapp.command.api.TransferCommand
import com.mayosen.financeapp.command.api.WithdrawCommand
import com.mayosen.financeapp.exception.UnsupportedCommandException
import org.springframework.stereotype.Component

@Component
class SyncCommandGateway(
    private val commandHandler: CommandHandler,
    private val adminCommandHandler: AdminCommandHandler,
) : CommandGateway {
    override fun <T : Command> send(command: T) {
        when (command) {
            is CreateAccountCommand -> commandHandler.handleCreateAccount(command)
            is DepositCommand -> commandHandler.handleDeposit(command)
            is WithdrawCommand -> commandHandler.handleWithdraw(command)
            is TransferCommand -> commandHandler.handleTransfer(command)
            else -> throw UnsupportedCommandException("${command::class}")
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any, R : Any> sendWithResult(command: T): R =
        when (command) {
            else -> throw UnsupportedCommandException("${command::class}")
        }

    override fun <T : AdminCommand> send(command: T) {
        when (command) {
            is ReplayEventsCommand -> adminCommandHandler.handleReplayEvents(command)
            else -> throw UnsupportedCommandException("${command::class}")
        }
    }
}
