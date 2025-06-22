package com.mayosen.financeapp.event.jdbc

const val EVENT_ID_FIELD = "eventId"
const val ACCOUNT_ID_FIELD = "accountId"
const val TIMESTAMP_ID_FIELD = "timestamp"

val EXPLICIT_FIELDS = setOf(EVENT_ID_FIELD, ACCOUNT_ID_FIELD, TIMESTAMP_ID_FIELD)

// TODO: Refactor

// class FieldRule<T>(
//     private val fieldName: String,
//     private val valueExtractor: (EventEntity) -> T,
// ) {
//     fun fieldName(): String = fieldName
//
//     fun fieldValue(event: EventEntity): T = valueExtractor.invoke(event)

// fun fieldValue(eventEntity: EventEntity): T {
//
// }
// }
//
// val EXPLICIT_FIELD_RULES =
//     listOf(
//         FieldRule(EVENT_ID_FIELD, { it.eventId }),
//         FieldRule(AGGREGATE_ID_FIELD, { it.accountId }),
//         FieldRule(TIMESTAMP_ID_FIELD, { it.timestamp }),
//     )
