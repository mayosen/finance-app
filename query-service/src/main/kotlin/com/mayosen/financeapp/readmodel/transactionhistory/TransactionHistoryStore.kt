package com.mayosen.financeapp.readmodel.transactionhistory

interface TransactionHistoryStore {
    fun findByAccountId(
        accountId: String,
        timePeriod: TimePeriod?,
        pagination: Pagination,
    ): TransactionHistory

    fun save(transaction: Transaction)

    fun saveAll(transactions: List<Transaction>)

    fun deleteAllByAccountId(accountId: String)
}
