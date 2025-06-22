package com.mayosen.financeapp.projection.transaction

interface TransactionStore {
    fun findByAccountId(
        accountId: String,
        timePeriod: TimePeriod?,
        pagination: Pagination,
    ): TransactionHistory

    fun save(transaction: Transaction)

    fun saveAll(transactions: List<Transaction>)

    fun deleteAllByAccountId(accountId: String)
}
