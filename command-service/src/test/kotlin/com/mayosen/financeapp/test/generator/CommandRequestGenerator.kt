package com.mayosen.financeapp.test.generator

import com.mayosen.financeapp.command.api.v1.CreateAccountRequest
import com.mayosen.financeapp.test.OWNER_ID

fun generateCreateAccountRequest(): CreateAccountRequest = CreateAccountRequest(ownerId = OWNER_ID)
