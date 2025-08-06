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
    override fun findByAccountId(accountId: String): AccountSnapshot? =
        accountSnapshotEntityRepository
            .findById(accountId)
            .map {
                AccountSnapshot(
                    accountId = it.accountId,
                    balance = it.balance,
                    lastSequenceNumber = it.lastSequenceNumber,
                    timestamp = it.timestamp,
                )
            }.getOrNull()

    override fun save(aggregate: AccountAggregate) {
        val maxSequenceNumber =
            eventEntityRepository.findMaxSequenceNumberByAccountId(aggregate.accountId)
                ?: 0
        val isNew = !accountSnapshotEntityRepository.existsById(aggregate.accountId)
        val entity =
            aggregate
                .toSnapshot(maxSequenceNumber)
                .let {
                    AccountSnapshotEntity(
                        accountId = it.accountId,
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
