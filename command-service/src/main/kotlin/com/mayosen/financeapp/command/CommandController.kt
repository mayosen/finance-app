package com.mayosen.financeapp.command

import com.mayosen.financeapp.command.api.CommandGateway
import com.mayosen.financeapp.command.api.v1.CommandApi
import com.mayosen.financeapp.command.api.v1.CommandResponse
import com.mayosen.financeapp.command.api.CreateAccountCommand
import com.mayosen.financeapp.command.api.v1.CreateAccountRequest
import com.mayosen.financeapp.command.api.DepositCommand
import com.mayosen.financeapp.command.api.v1.DepositRequest
import com.mayosen.financeapp.command.api.TransferCommand
import com.mayosen.financeapp.command.api.v1.TransferRequest
import com.mayosen.financeapp.command.api.WithdrawCommand
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
    override fun createAccount(request: CreateAccountRequest): ResponseEntity<CommandResponse> {
        logger.info("Creating account $request")
        val command =
            CreateAccountCommand(
                id = generateCommandId(),
                ownerId = request.ownerId,
            )
        return sendCommandReturningId(command)
    }

    override fun deposit(request: DepositRequest): ResponseEntity<CommandResponse> {
        logger.info { "Received DepositCommand: $request" }
        val command =
            DepositCommand(
                id = generateCommandId(),
                accountId = request.accountId,
                amount = request.amount,
            )
        return sendCommandReturningId(command)
    }

    override fun withdraw(request: WithdrawRequest): ResponseEntity<CommandResponse> {
        logger.info { "Received WithdrawCommand: $request" }
        val command =
            WithdrawCommand(
                id = generateCommandId(),
                accountId = request.accountId,
                amount = request.amount,
            )
        return sendCommandReturningId(command)
    }

    override fun transfer(request: TransferRequest): ResponseEntity<CommandResponse> {
        logger.info { "Received TransferCommand: $request" }
        val command =
            TransferCommand(
                id = generateCommandId(),
                fromAccountId = request.fromAccountId,
                toAccountId = request.toAccountId,
                amount = request.amount,
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
