package com.mayosen.financeapp.util

import java.util.*

object IdGenerator {
    fun generateTransactionId(): String = generateId()

    private fun generateId(): String = UUID.randomUUID().toString()
}
