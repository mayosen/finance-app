package com.mayosen.financeapp.readmodel.transactionhistory.jdbc

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Transient
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal
import java.time.Instant

@Table("transaction_view")
data class TransactionViewEntity(
    @Id
    val transactionId: String,
    val accountId: String,
    val sourceEventId: String,
    val type: TransactionType,
    val amount: BigDecimal,
    val timestamp: Instant,
    val relatedAccountId: String?,
    @Transient
    val isNewEntity: Boolean,
) : Persistable<String> {
    constructor(
        transactionId: String,
        accountId: String,
        sourceEventId: String,
        type: TransactionType,
        amount: BigDecimal,
        timestamp: Instant,
        relatedAccountId: String?,
    ) : this(
        transactionId = transactionId,
        accountId = accountId,
        sourceEventId = sourceEventId,
        type = type,
        amount = amount,
        timestamp = timestamp,
        relatedAccountId = relatedAccountId,
        isNewEntity = false,
    )

    enum class TransactionType {
        DEPOSIT,
        WITHDRAWAL,
        TRANSFER_OUT,
        TRANSFER_IN,
    }

    override fun getId(): String = transactionId

    override fun isNew(): Boolean = isNewEntity
}
