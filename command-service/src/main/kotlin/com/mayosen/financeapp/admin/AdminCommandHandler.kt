package com.mayosen.financeapp.admin

import com.mayosen.financeapp.command.api.ReplayEventsCommand
import com.mayosen.financeapp.event.EventPublisher
import com.mayosen.financeapp.event.EventStore
import com.mayosen.financeapp.event.ResetReadModelEvent
import com.mayosen.financeapp.util.IdGenerator.generateEventId
import org.springframework.stereotype.Service

@Service
class AdminCommandHandler(
    private val eventStore: EventStore,
    private val eventPublisher: EventPublisher,
) {
    fun handleReplayEvents(command: ReplayEventsCommand) {
        val resetReadModelEvent =
            ResetReadModelEvent(
                eventId = generateEventId(),
                accountId = command.accountId,
            )
        val businessEvents = eventStore.findAllByAccountId(command.accountId)
        eventPublisher.publish(resetReadModelEvent)
        eventPublisher.publishAll(businessEvents)
    }
}
