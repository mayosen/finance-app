package com.mayosen.financeapp.event

/**
 * Публикует события.
 */
interface EventPublisher {
    fun publish(event: Event)

    fun publishAll(events: List<Event>)
}
