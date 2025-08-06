package com.mayosen.financeapp.event.jdbc

import com.mayosen.financeapp.event.Event

const val EVENT_ID_FIELD = "eventId"
const val AGGREGATE_ID_FIELD = "aggregateId"
const val TIMESTAMP_ID_FIELD = "timestamp"

val EXPLICIT_FIELDS = setOf(EVENT_ID_FIELD, AGGREGATE_ID_FIELD, TIMESTAMP_ID_FIELD)

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
//         FieldRule(AGGREGATE_ID_FIELD, { it.aggregateId }),
//         FieldRule(TIMESTAMP_ID_FIELD, { it.timestamp }),
//     )
