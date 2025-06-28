package com.mayosen.financeapp.event

import java.math.BigDecimal
import java.time.Instant

data class AccountCreatedEvent(
    override val eventId: String,
    override val accountId: String,
    override val timestamp: Instant = Instant.now(),
    val ownerId: String,
) : Event

data class TransferPerformedEvent(
    override val eventId: String,
    override val accountId: String,
    override val timestamp: Instant = Instant.now(),
    val toAccountId: String,
    val amount: BigDecimal,
) : Event

data class DepositPerformedEvent(
    override val eventId: String,
    override val accountId: String,
    override val timestamp: Instant = Instant.now(),
    val amount: BigDecimal,
) : Event

data class WithdrawalPerformedEvent(
    override val eventId: String,
    override val accountId: String,
    override val timestamp: Instant = Instant.now(),
    val amount: BigDecimal,
) : Event

data class AccountDeletedEvent(
    override val eventId: String,
    override val accountId: String,
    override val timestamp: Instant = Instant.now(),
) : Event
