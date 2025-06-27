package com.mayosen.financeapp.test

import com.mayosen.financeapp.test.util.toCurrency
import java.math.BigDecimal
import java.time.Instant

// Transaction
const val TRANSACTION_ID = "transaction_id_1"

// Account
const val ACCOUNT_ID = "account_id_1"
const val OWNER_ID = "owner_id"

// Event
const val EVENT_ID = "event_id_1"
const val EVENT_ID_2 = "event_id_2"
const val EVENT_ID_3 = "event_id_3"
const val EVENT_ID_4 = "event_id_4"

// Balance
val AMOUNT_100 = 100.toBigDecimal().toCurrency()
val AMOUNT_50 = 50.toBigDecimal().toCurrency()
val AMOUNT_0 = BigDecimal.ZERO.toCurrency()

// Time
val INSTANT: Instant = Instant.parse("2025-06-01T00:00:00.000Z")
