package com.mayosen.financeapp.event.jdbc.serialization.rules

interface RulesProvider {
    fun getRules(): List<ExplicitFieldRule<*>>
}
