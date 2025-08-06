package com.mayosen.financeapp.readmodel.accountsummary

interface AccountSummaryStore {
    fun findAllByOwnerId(ownerId: String): List<AccountSummary>

    fun findByAccountId(accountId: String): AccountSummary?

    fun save(summary: AccountSummary)

    fun saveAll(summaries: List<AccountSummary>)

    fun deleteByAccountId(accountId: String)
}
