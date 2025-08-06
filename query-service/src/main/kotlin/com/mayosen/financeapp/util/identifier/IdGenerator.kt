package com.mayosen.financeapp.util.identifier

interface IdGenerator {
    fun generateTransactionId(): String

    fun generateEventId(): String

    fun generateAccountId(): String
}
