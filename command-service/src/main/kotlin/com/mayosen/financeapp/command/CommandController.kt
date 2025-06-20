package com.mayosen.financeapp.command

import com.mayosen.financeapp.command.api.CommandGateway
import com.mayosen.financeapp.command.api.CreateAccountCommand
import com.mayosen.financeapp.command.api.DeleteAccountCommand
import com.mayosen.financeapp.command.api.DepositCommand
import com.mayosen.financeapp.command.api.TransferCommand
import com.mayosen.financeapp.command.api.WithdrawCommand
import com.mayosen.financeapp.command.api.v1.CommandApi
import com.mayosen.financeapp.command.api.v1.CommandResponse
import com.mayosen.financeapp.command.api.v1.CreateAccountRequest
import com.mayosen.financeapp.command.api.v1.DeleteAccountRequest
import com.mayosen.financeapp.command.api.v1.DepositRequest
import com.mayosen.financeapp.command.api.v1.TransferRequest
import com.mayosen.financeapp.command.api.v1.WithdrawRequest
import com.mayosen.financeapp.util.IdGenerator.generateCommandId
import org.apache.logging.log4j.kotlin.Logging
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

/**
 * REST API для приема команд.
 */
@RestController
class CommandController(
    private val commandGateway: CommandGateway,
) : CommandApi {
    override fun createAccount(createAccountRequest: CreateAccountRequest): ResponseEntity<CommandResponse> {
        logger.info { "Received CreateAccountRequest: $createAccountRequest" }
        val command =
            CreateAccountCommand(
                id = generateCommandId(),
                ownerId = createAccountRequest.ownerId,
            )
        return sendCommandReturningId(command)
    }

    override fun deposit(depositRequest: DepositRequest): ResponseEntity<CommandResponse> {
        logger.info { "Received DepositRequest: $depositRequest" }
        val command =
            DepositCommand(
                id = generateCommandId(),
                accountId = depositRequest.accountId,
                amount = depositRequest.amount,
            )
        return sendCommandReturningId(command)
    }

    override fun withdraw(withdrawRequest: WithdrawRequest): ResponseEntity<CommandResponse> {
        logger.info { "Received WithdrawRequest: $withdrawRequest" }
        val command =
            WithdrawCommand(
                id = generateCommandId(),
                accountId = withdrawRequest.accountId,
                amount = withdrawRequest.amount,
            )
        return sendCommandReturningId(command)
    }

    override fun transfer(transferRequest: TransferRequest): ResponseEntity<CommandResponse> {
        logger.info { "Received TransferRequest: $transferRequest" }
        val command =
            TransferCommand(
                id = generateCommandId(),
                fromAccountId = transferRequest.fromAccountId,
                toAccountId = transferRequest.toAccountId,
                amount = transferRequest.amount,
            )
        return sendCommandReturningId(command)
    }

    override fun deleteAccount(deleteAccountRequest: DeleteAccountRequest): ResponseEntity<CommandResponse> {
        logger.info { "Received DeleteAccountRequest: $deleteAccountRequest" }
        val command =
            DeleteAccountCommand(
                id = generateCommandId(),
                accountId = deleteAccountRequest.accountId,
            )
        return sendCommandReturningId(command)
    }

    private fun sendCommandReturningId(command: Command): ResponseEntity<CommandResponse> {
        commandGateway.send(command)
        val response = CommandResponse(id = command.id)
        return ResponseEntity.ok(response)
    }

    companion object : Logging
}
