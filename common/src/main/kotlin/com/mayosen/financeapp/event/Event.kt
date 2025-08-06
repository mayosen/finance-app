package com.mayosen.financeapp.event

import java.time.Instant

interface Event {
    val eventId: String
    val accountId: String
    val timestamp: Instant
}
