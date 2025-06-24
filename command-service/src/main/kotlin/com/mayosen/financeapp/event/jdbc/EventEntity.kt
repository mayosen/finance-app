package com.mayosen.financeapp.event.jdbc

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.PersistenceCreator
import org.springframework.data.annotation.Transient
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("event")
data class EventEntity(
    @Id
    val eventId: String,
    val sequenceNumber: Long,
    val accountId: String,
    val eventType: String,
    val eventFields: String,
    val timestamp: Instant,
    @Transient
    val isNewEntity: Boolean,
) : Persistable<String> {
    @Suppress("unused")
    @PersistenceCreator // See https://github.com/spring-projects/spring-data-relational/issues/809
    constructor(
        eventId: String,
        sequenceNumber: Long,
        accountId: String,
        eventType: String,
        eventFields: String,
        timestamp: Instant,
    ) : this(
        eventId = eventId,
        sequenceNumber = sequenceNumber,
        accountId = accountId,
        eventType = eventType,
        eventFields = eventFields,
        timestamp = timestamp,
        isNewEntity = false,
    )

    override fun getId(): String = eventId

    override fun isNew(): Boolean = isNewEntity
}
