package com.mayosen.financeapp.command.api

import com.mayosen.financeapp.command.Command

interface CommandGateway {
    fun <T : Command> send(command: T)

    fun <T : AdminCommand> send(command: T)
}
