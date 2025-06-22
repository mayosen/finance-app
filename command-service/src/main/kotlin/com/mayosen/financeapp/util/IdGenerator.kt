package com.mayosen.financeapp.util

interface IdGenerator {
    fun generateCommandId(): String

    fun generateEventId(): String

    fun generateAccountId(): String
}
