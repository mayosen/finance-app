package com.mayosen.financeapp.test

import com.mayosen.financeapp.test.util.toCurrency
import java.math.BigDecimal
import java.time.Instant

// Transaction
const val TRANSACTION_ID = "transaction_id"

// Event
const val EVENT_ID = "event_id"

// Account
const val ACCOUNT_ID = "account_id"
const val OWNER_ID = "owner_id"

// Balance
val BALANCE_100 = 100.toBigDecimal().toCurrency()
val BALANCE_50 = 50.toBigDecimal().toCurrency()
val BALANCE_0 = BigDecimal.ZERO.toCurrency()

// Time
val INSTANT = Instant.parse("2025-06-01T00:00:00.000Z")

