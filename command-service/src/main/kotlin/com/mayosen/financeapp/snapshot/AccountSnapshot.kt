package com.mayosen.financeapp.snapshot

import java.math.BigDecimal
import java.time.Instant

/**
 * Снимок состояния агрегата Account для оптимизации восстановления.
 */
data class AccountSnapshot(
    val accountId: String,
    val balance: BigDecimal,
    val created: Boolean, // TODO: Может снимку и не нужно это поле?
    val lastSequenceNumber: Long,
    val timestamp: Instant = Instant.now(),
)
