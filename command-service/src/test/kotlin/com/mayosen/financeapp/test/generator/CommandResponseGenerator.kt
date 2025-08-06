package com.mayosen.financeapp.test.generator

import com.mayosen.financeapp.command.api.admin.AdminCommandResponse
import com.mayosen.financeapp.command.api.v1.CommandResponse
import com.mayosen.financeapp.test.COMMAND_ID

fun generateCommandResponse(id: String = COMMAND_ID): CommandResponse = CommandResponse(id = id)

fun generateAdminCommandResponse(id: String = COMMAND_ID): AdminCommandResponse = AdminCommandResponse(id = id)
