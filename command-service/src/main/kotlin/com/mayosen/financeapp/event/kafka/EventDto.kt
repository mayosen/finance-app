package com.mayosen.financeapp.event.kafka

import com.mayosen.financeapp.event.Event

data class EventDto(
    val type: String,
    val event: Event,
)
