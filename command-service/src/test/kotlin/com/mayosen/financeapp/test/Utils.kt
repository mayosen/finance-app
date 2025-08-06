package com.mayosen.financeapp.test

import java.math.BigDecimal
import java.math.RoundingMode

fun BigDecimal.toCurrency(): BigDecimal = setScale(2, RoundingMode.UNNECESSARY)
