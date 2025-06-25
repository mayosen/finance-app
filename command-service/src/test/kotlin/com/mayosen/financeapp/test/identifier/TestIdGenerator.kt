package com.mayosen.financeapp.test.identifier

import com.mayosen.financeapp.util.IdGenerator

// TODO: Copy to query service?

object TestIdGenerator : IdGenerator {
    private val commandGenerator = StringIdGenerator("command_id")
    private val eventGenerator = StringIdGenerator("event_id")
    private val accountGenerator = StringIdGenerator("account_id")

    override fun generateCommandId(): String = commandGenerator.generate()

    override fun generateEventId(): String = eventGenerator.generate()

    override fun generateAccountId(): String = accountGenerator.generate()

    fun reset() {
        commandGenerator.reset()
        eventGenerator.reset()
        accountGenerator.reset()
    }
}
