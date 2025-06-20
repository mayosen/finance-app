package com.mayosen.financeapp.event

import kotlin.reflect.KClass

/**
 * Хранилище событий
 */
interface EventStore {
    fun countByAccountId(accountId: String): Int

    fun findAllByAccountId(accountId: String): List<Event>

    fun findAllByAccountIdAfterSequenceNumber(
        accountId: String,
        sequenceNumber: Long,
    ): List<Event>

    fun findAllByAccountIdAndTypeIn(
        accountId: String,
        types: List<KClass<out Event>>,
    ): List<Event>

    fun save(event: Event)

    fun saveAll(events: List<Event>)
}
