package com.mayosen.financeapp.test.assertions

import org.assertj.core.api.AbstractInstantAssert
import org.assertj.core.api.Assertions.byLessThan
import java.time.Instant
import java.time.temporal.ChronoUnit

fun AbstractInstantAssert<*>.isCloseToNow(seconds: Int = 1): AbstractInstantAssert<*> =
    isCloseTo(
        Instant.now(),
        byLessThan(seconds.toLong(), ChronoUnit.SECONDS),
    )
