package com.mayosen.financeapp.snapshot.jdbc

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.PersistenceCreator
import org.springframework.data.annotation.Transient
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal
import java.time.Instant

// TODO: Indexes

@Table("snapshot")
data class AccountSnapshotEntity(
    @Id
    val accountId: String,
    val balance: BigDecimal,
    val lastSequenceNumber: Long,
    val timestamp: Instant = Instant.now(),
    @Transient
    val isNewEntity: Boolean,
) : Persistable<String> {
    @Suppress("unused")
    @PersistenceCreator
    constructor(
        accountId: String,
        balance: BigDecimal,
        lastSequenceNumber: Long,
        timestamp: Instant = Instant.now(),
    ) : this(
        accountId = accountId,
        balance = balance,
        lastSequenceNumber = lastSequenceNumber,
        timestamp = timestamp,
        isNewEntity = false,
    )

    override fun getId(): String = accountId

    override fun isNew(): Boolean = isNewEntity
}
