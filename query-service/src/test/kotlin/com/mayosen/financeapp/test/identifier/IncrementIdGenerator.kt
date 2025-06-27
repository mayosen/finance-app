package com.mayosen.financeapp.test.identifier

class IncrementIdGenerator {
    private var counter = START_VALUE

    fun generate(): Long = counter++

    fun reset() {
        counter = START_VALUE
    }

    companion object {
        const val START_VALUE = 1L
    }
}
