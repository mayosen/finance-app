package com.mayosen.financeapp.snapshot

import com.mayosen.financeapp.aggregate.AccountAggregate
import com.mayosen.financeapp.event.EventStore
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class CreateSnapshotByTimesStrategy(
    @Value("\${app.snapshot.strategy.every-times}")
    private val timesNumber: Int,
    private val eventStore: EventStore,
) : CreateSnapshotStrategy {
    override fun shouldCreateSnapshot(aggregate: AccountAggregate): Boolean {
        val totalEvents = eventStore.countByAccountId(aggregate.accountId)
        return aggregate.created && totalEvents % timesNumber == 0
    }
}
