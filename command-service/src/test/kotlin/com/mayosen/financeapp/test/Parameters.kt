package com.mayosen.financeapp.test

import com.mayosen.financeapp.test.util.toCurrency
import java.math.BigDecimal
import java.time.Instant

// Command
const val COMMAND_ID = "command_id"

// CreateAccountCommand
const val OWNER_ID = "owner_id"
const val ACCOUNT_ID = "account_id"

// Event
const val EVENT_ID = "event_id"
const val LAST_SEQUENCE_NUMBER = 0L

val BALANCE_100 = 100.toBigDecimal().toCurrency()
val BALANCE_50 = 50.toBigDecimal().toCurrency()
val BALANCE_0 = BigDecimal.ZERO.toCurrency()

val INSTANT = Instant.parse("2025-06-01T00:00:00.000Z")
