package com.mayosen.financeapp.event.jdbc.serialization.rules

import org.springframework.stereotype.Component

@Component
class DefaultRulesProvider : RulesProvider {
    override fun getRules(): List<ExplicitFieldRule<*>> = RULES

    private companion object {
        const val EVENT_ID_FIELD = "eventId"
        const val ACCOUNT_ID_FIELD = "accountId"
        const val TIMESTAMP_ID_FIELD = "timestamp"

        val RULES =
            listOf(
                StringExplicitFieldRule(EVENT_ID_FIELD) { it.eventId },
                StringExplicitFieldRule(ACCOUNT_ID_FIELD) { it.accountId },
                StringExplicitFieldRule(TIMESTAMP_ID_FIELD) { it.timestamp.toString() },
            )
    }
}
