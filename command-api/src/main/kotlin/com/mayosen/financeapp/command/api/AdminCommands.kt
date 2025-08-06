package com.mayosen.financeapp.command.api

import com.mayosen.financeapp.command.Command

interface AdminCommand : Command

data class ReplayEventsCommand(
    override val id: String,
    val accountId: String,
) : AdminCommand
