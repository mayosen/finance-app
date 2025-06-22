package com.mayosen.financeapp.util

interface IdGenerator {
    fun generateTransactionId(): String

    @Deprecated("Unused?")
    fun generateEventId(): String

    @Deprecated("Unused?")
    fun generateAccountId(): String
}
