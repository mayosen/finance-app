package com.mayosen.financeapp.test

import com.mayosen.financeapp.test.util.toCurrency
import java.math.BigDecimal
import java.time.Instant

// Command
const val COMMAND_ID = "command_id_1"
const val COMMAND_ID_2 = "command_id_2"

// CreateAccountCommand
const val OWNER_ID = "owner_id"
const val ACCOUNT_ID = "account_id_1"

// Event
const val EVENT_ID = "event_id_1"
const val EVENT_ID_2 = "event_id_2"
const val EVENT_ID_3 = "event_id_3"
const val LAST_SEQUENCE_NUMBER = 0L
const val SEQUENCE_NUMBER = 1L
const val SEQUENCE_NUMBER_2 = 2L

// Amount
val AMOUNT_100 = 100.toBigDecimal().toCurrency()
val AMOUNT_50 = 50.toBigDecimal().toCurrency()
val AMOUNT_0 = BigDecimal.ZERO.toCurrency()

// Time
val INSTANT = Instant.parse("2025-06-01T00:00:00.000Z")
