package com.mayosen.financeapp.util.identifier

import org.springframework.stereotype.Component
import java.util.UUID

@Component
class UuidIdGenerator : IdGenerator {
    override fun generateTransactionId(): String = generateUuid()

    override fun generateEventId(): String = generateUuid()

    override fun generateAccountId(): String = generateUuid()

    private fun generateUuid(): String = UUID.randomUUID().toString()
}
