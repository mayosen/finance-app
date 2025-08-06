package com.mayosen.financeapp.event

import java.time.Instant

data class ResetProjectionsEvent(
    override val eventId: String,
    override val accountId: String,
    override val timestamp: Instant = Instant.now(),
) : ServiceEvent
