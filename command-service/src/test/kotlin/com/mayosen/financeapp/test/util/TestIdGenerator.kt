package com.mayosen.financeapp.test.util

import com.mayosen.financeapp.test.ACCOUNT_ID
import com.mayosen.financeapp.test.COMMAND_ID
import com.mayosen.financeapp.test.EVENT_ID
import com.mayosen.financeapp.util.IdGenerator

object TestIdGenerator : IdGenerator {
    override fun generateCommandId(): String = COMMAND_ID

    override fun generateEventId(): String = EVENT_ID

    override fun generateAccountId(): String = ACCOUNT_ID
}
