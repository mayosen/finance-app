package com.mayosen.financeapp.command.api.v1

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping

/**
 * REST API для приема команд.
 */
@RequestMapping("/api/v1/commands")
interface CommandApi {
    @PostMapping("/accounts")
    fun createAccount(
        @RequestBody request: CreateAccountRequest,
    ): ResponseEntity<CommandResponse>

    @PostMapping("/deposit")
    fun deposit(
        @RequestBody request: DepositRequest,
    ): ResponseEntity<CommandResponse>

    @PostMapping("/withdraw")
    fun withdraw(
        @RequestBody request: WithdrawRequest,
    ): ResponseEntity<CommandResponse>

    @PostMapping("/transfer")
    fun transfer(
        @RequestBody request: TransferRequest,
    ): ResponseEntity<CommandResponse>
}
