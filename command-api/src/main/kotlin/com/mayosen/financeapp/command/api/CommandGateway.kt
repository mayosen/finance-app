package com.mayosen.financeapp.command.api

import com.mayosen.financeapp.command.Command

interface CommandGateway {
    fun <T : Command> send(command: T)

    @Deprecated("Unused")
    fun <T : Any, R : Any> sendWithResult(command: T): R

    fun <T : AdminCommand> send(command: T)
}
