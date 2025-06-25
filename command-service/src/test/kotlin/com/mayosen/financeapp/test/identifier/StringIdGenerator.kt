package com.mayosen.financeapp.test.identifier

class StringIdGenerator(
    private val prefix: String,
) {
    private val generator = IncrementIdGenerator()

    fun generate(): String = "${prefix}_${generator.generate()}"

    fun reset() = generator.reset()
}
