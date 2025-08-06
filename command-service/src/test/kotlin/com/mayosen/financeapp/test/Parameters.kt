package com.mayosen.financeapp.test

import java.math.BigDecimal
import java.time.Instant

const val OWNER_ID = "owner"
const val ACCOUNT_ID = "ec6fdd75-541b-489a-9e92-c9f48b7ced52"
const val EVENT_ID = "3f8d9fe6-4b41-4abb-9840-98b99426dcc6"

const val LAST_SEQUENCE_NUMBER = 0L

val BALANCE_100 = 100.toBigDecimal().toCurrency()
val BALANCE_50 = 50.toBigDecimal().toCurrency()
val BALANCE_0 = BigDecimal.ZERO.toCurrency()

val INSTANT = Instant.parse("2025-06-01T00:00:00.000Z")
