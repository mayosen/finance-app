package com.mayosen.financeapp.util

import java.util.*

object IdGenerator {
    fun generateCommandId(): String = generateId()

    fun generateEventId(): String = generateId()

    fun generateAccountId(): String = generateId()

    private fun generateId(): String = UUID.randomUUID().toString()
}
