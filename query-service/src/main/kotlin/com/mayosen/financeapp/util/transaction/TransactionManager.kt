package com.mayosen.financeapp.util.transaction

interface TransactionManager {
    fun executeInTransaction(action: () -> (Unit))
}
