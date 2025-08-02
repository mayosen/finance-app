package com.mayosen.financeapp.projection.account

import com.mayosen.financeapp.event.Event

interface AccountSummaryStore {
    fun hasBeenUpdatedBy(event: Event): Boolean

    fun findAllByOwnerId(ownerId: String): List<AccountSummary>

    fun findByAccountId(accountId: String): AccountSummary?

    fun save(summary: AccountSummary)

    fun saveAll(summaries: List<AccountSummary>)

    fun deleteByAccountId(accountId: String)
}
