package com.mayosen.financeapp.snapshot.jdbc

import com.mayosen.financeapp.aggregate.AccountAggregate
import com.mayosen.financeapp.event.jdbc.EventEntityRepository
import com.mayosen.financeapp.snapshot.AccountSnapshot
import com.mayosen.financeapp.snapshot.SnapshotStore
import org.springframework.stereotype.Component
import kotlin.jvm.optionals.getOrNull

@Component
class JdbcSnapshotStore(
    private val accountSnapshotEntityRepository: AccountSnapshotEntityRepository,
    private val eventEntityRepository: EventEntityRepository,
) : SnapshotStore {
    override fun findByAggregateId(aggregateId: String): AccountSnapshot? =
        accountSnapshotEntityRepository
            .findById(aggregateId)
            .getOrNull()
            ?.let {
                AccountSnapshot(
                    accountId = it.aggregateId,
                    balance = it.balance,
                    created = true,
                    lastSequenceNumber = it.lastSequenceNumber,
                    timestamp = it.timestamp,
                )
            }

    override fun save(aggregate: AccountAggregate) {
        val maxSequenceNumber =
            eventEntityRepository.findMaxSequenceNumberByAggregateId(aggregate.accountId)
                ?: 0
        val isNew = !accountSnapshotEntityRepository.existsById(aggregate.accountId)
        val entity =
            aggregate
                .toSnapshot(maxSequenceNumber)
                .let {
                    AccountSnapshotEntity(
                        aggregateId = it.accountId,
                        balance = it.balance,
                        lastSequenceNumber = it.lastSequenceNumber,
                        isNewEntity = isNew,
                    )
                }
        accountSnapshotEntityRepository.save(entity)
    }

    override fun delete(aggregate: AccountAggregate) {
        accountSnapshotEntityRepository.deleteById(aggregate.accountId)
    }
}
