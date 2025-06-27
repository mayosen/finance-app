package com.mayosen.financeapp.util

interface IdGenerator {
    fun generateTransactionId(): String

    fun generateEventId(): String

    fun generateAccountId(): String
}
