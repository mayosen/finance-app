package com.mayosen.financeapp.event

import java.time.Instant

interface Event {
    val eventId: String
    val aggregateId: String
    val timestamp: Instant
}
