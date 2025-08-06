package com.mayosen.financeapp.event

import java.math.BigDecimal
import java.time.Instant

// TODO: generate event id by default

data class AccountCreatedEvent(
    override val eventId: String,
    override val aggregateId: String,
    override val timestamp: Instant = Instant.now(),
    val ownerId: String,
) : Event

data class TransferPerformedEvent(
    override val eventId: String,
    override val aggregateId: String,
    override val timestamp: Instant = Instant.now(),
    val toAggregateId: String,
    val amount: BigDecimal,
) : Event

data class DepositPerformedEvent(
    override val eventId: String,
    override val aggregateId: String,
    override val timestamp: Instant = Instant.now(),
    val amount: BigDecimal,
) : Event

data class WithdrawalPerformedEvent(
    override val eventId: String,
    override val aggregateId: String,
    override val timestamp: Instant = Instant.now(),
    val amount: BigDecimal,
) : Event
