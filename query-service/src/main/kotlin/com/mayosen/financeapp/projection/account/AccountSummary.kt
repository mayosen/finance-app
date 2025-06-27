package com.mayosen.financeapp.projection.account

import java.math.BigDecimal
import java.time.Instant

/**
 * Проекция для быстрого чтения баланса.
 */
data class AccountSummary(
    val accountId: String,
    val balance: BigDecimal,
    val ownerId: String,
    val updatedAt: Instant,
    val sourceEventId: String,
)
