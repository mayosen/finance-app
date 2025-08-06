package com.mayosen.financeapp.projection.transaction

import com.mayosen.financeapp.event.Event

interface TransactionStore {
    fun hasBeenUpdatedBy(event: Event): Boolean

    fun findByAccountId(
        accountId: String,
        timePeriod: TimePeriod?,
        pagination: Pagination,
    ): TransactionHistory

    fun save(transaction: Transaction)

    fun saveAll(transactions: List<Transaction>)

    fun deleteAllByAccountId(accountId: String)
}
