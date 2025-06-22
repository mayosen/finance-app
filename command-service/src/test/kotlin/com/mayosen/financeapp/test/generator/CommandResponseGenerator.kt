package com.mayosen.financeapp.test.generator

import com.mayosen.financeapp.command.api.v1.CommandResponse
import com.mayosen.financeapp.test.COMMAND_ID

fun generateCommandResponse(): CommandResponse = CommandResponse(id = COMMAND_ID)
