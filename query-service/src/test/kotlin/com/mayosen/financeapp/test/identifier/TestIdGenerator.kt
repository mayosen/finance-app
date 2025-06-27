package com.mayosen.financeapp.test.identifier

import com.mayosen.financeapp.util.IdGenerator

object TestIdGenerator : IdGenerator {
    private val transactionGenerator = StringIdGenerator("transaction_id")
    private val eventGenerator = StringIdGenerator("event_id")
    private val accountGenerator = StringIdGenerator("account_id")

    override fun generateTransactionId(): String = transactionGenerator.generate()

    override fun generateEventId(): String = eventGenerator.generate()

    override fun generateAccountId(): String = accountGenerator.generate()

    fun reset() {
        transactionGenerator.reset()
        eventGenerator.reset()
        accountGenerator.reset()
    }
}
