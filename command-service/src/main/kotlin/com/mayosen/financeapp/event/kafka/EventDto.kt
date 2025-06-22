package com.mayosen.financeapp.event.kafka

import com.mayosen.financeapp.event.Event

@Deprecated("Remove. Serialize plain event")
data class EventDto(
    val type: String,
    val event: Event,
)
