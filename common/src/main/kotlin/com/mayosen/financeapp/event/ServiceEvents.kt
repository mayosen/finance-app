package com.mayosen.financeapp.event

import java.time.Instant

interface ServiceEvent : Event

data class ResetReadModelEvent(
    override val eventId: String,
    override val aggregateId: String,
    override val timestamp: Instant = Instant.now(),
) : ServiceEvent
