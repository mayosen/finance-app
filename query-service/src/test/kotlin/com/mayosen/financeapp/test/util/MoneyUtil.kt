package com.mayosen.financeapp.test.util

import java.math.BigDecimal
import java.math.RoundingMode

fun BigDecimal.toCurrency(): BigDecimal = setScale(2, RoundingMode.HALF_UP)
