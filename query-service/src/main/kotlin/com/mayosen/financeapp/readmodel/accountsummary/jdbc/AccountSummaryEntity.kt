package com.mayosen.financeapp.readmodel.accountsummary.jdbc

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.PersistenceCreator
import org.springframework.data.annotation.Transient
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal
import java.time.Instant

@Table("account_summary")
data class AccountSummaryEntity(
    @Id
    val accountId: String,
    val ownerId: String,
    val balance: BigDecimal,
    val updatedAt: Instant,
    @Transient
    val isNewEntity: Boolean,
) : Persistable<String> {
    @Suppress("unused")
    @PersistenceCreator
    constructor(
        accountId: String,
        ownerId: String,
        balance: BigDecimal,
        updatedAt: Instant,
    ) : this(
        accountId = accountId,
        ownerId = ownerId,
        balance = balance,
        updatedAt = updatedAt,
        isNewEntity = false,
    )

    override fun getId(): String = accountId

    override fun isNew(): Boolean = isNewEntity
}
