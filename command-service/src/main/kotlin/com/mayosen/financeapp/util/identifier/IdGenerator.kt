package com.mayosen.financeapp.util.identifier

interface IdGenerator {
    fun generateCommandId(): String

    fun generateEventId(): String

    fun generateAccountId(): String
}
