package com.mayosen.financeapp.util

import org.springframework.stereotype.Component
import java.util.UUID

@Component
class UuidIdGenerator : IdGenerator {
    override fun generateCommandId(): String = generateUuid()

    override fun generateEventId(): String = generateUuid()

    override fun generateAccountId(): String = generateUuid()

    private fun generateUuid(): String = UUID.randomUUID().toString()
}
