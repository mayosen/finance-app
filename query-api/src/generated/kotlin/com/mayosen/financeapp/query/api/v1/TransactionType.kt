package com.mayosen.financeapp.query.api.v1

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

/**
 *
 * Values: DEPOSIT,WITHDRAWAL,TRANSFER_OUT,TRANSFER_IN
 */
enum class TransactionType(
    @get:JsonValue val value: String,
) {
    DEPOSIT("DEPOSIT"),
    WITHDRAWAL("WITHDRAWAL"),
    TRANSFER_OUT("TRANSFER_OUT"),
    TRANSFER_IN("TRANSFER_IN"),
    ;

    companion object {
        @JvmStatic
        @JsonCreator
        fun forValue(value: String): TransactionType = values().first { it -> it.value == value }
    }
}
