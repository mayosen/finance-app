package com.mayosen.financeapp.util.transaction.jdbc

import com.mayosen.financeapp.util.transaction.TransactionManager
import org.springframework.stereotype.Component
import org.springframework.transaction.support.TransactionTemplate

@Component
class JdbcTransactionManager(
    private val transactionTemplate: TransactionTemplate,
) : TransactionManager {
    override fun executeInTransaction(action: () -> Unit) {
        transactionTemplate.executeWithoutResult {
            action()
        }
    }
}
