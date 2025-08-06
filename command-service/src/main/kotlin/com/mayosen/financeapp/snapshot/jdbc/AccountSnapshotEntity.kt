package com.mayosen.financeapp.snapshot.jdbc

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.PersistenceCreator
import org.springframework.data.annotation.Transient
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal
import java.time.Instant

// TODO: Indexes

@Table("SNAPSHOT")
class AccountSnapshotEntity(
    @Id
    val aggregateId: String,
    val balance: BigDecimal,
    val lastSequenceNumber: Long,
    val timestamp: Instant = Instant.now(),
    @Transient
    val isNewEntity: Boolean,
) : Persistable<String> {
    @Suppress("unused")
    @PersistenceCreator
    constructor(
        aggregateId: String,
        balance: BigDecimal,
        lastSequenceNumber: Long,
        timestamp: Instant = Instant.now(),
    ) : this(
        aggregateId = aggregateId,
        balance = balance,
        lastSequenceNumber = lastSequenceNumber,
        timestamp = timestamp,
        isNewEntity = false,
    )

    override fun getId(): String = aggregateId

    override fun isNew(): Boolean = isNewEntity
}
