package com.mayosen.financeapp.admin

import com.mayosen.financeapp.command.api.AdminCommand
import com.mayosen.financeapp.command.api.CommandGateway
import com.mayosen.financeapp.command.api.ReplayEventsCommand
import com.mayosen.financeapp.command.api.admin.AdminApi
import com.mayosen.financeapp.command.api.admin.AdminCommandResponse
import com.mayosen.financeapp.command.api.admin.ReplayEventsRequest
import com.mayosen.financeapp.util.IdGenerator
import org.apache.logging.log4j.kotlin.Logging
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class AdminController(
    private val commandGateway: CommandGateway,
    private val idGenerator: IdGenerator,
) : AdminApi {
    override fun replayEvents(replayEventsRequest: ReplayEventsRequest): ResponseEntity<AdminCommandResponse> {
        logger.info { "Received ReplayEventsRequest: $replayEventsRequest" }
        val command =
            ReplayEventsCommand(
                id = idGenerator.generateCommandId(),
                accountId = replayEventsRequest.accountId,
            )
        return sendCommandReturningId(command)
    }

    private fun sendCommandReturningId(command: AdminCommand): ResponseEntity<AdminCommandResponse> {
        commandGateway.send(command)
        val response = AdminCommandResponse(id = command.id)
        return ResponseEntity.ok(response)
    }

    private companion object : Logging
}
