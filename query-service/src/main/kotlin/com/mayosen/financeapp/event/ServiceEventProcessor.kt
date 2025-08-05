package com.mayosen.financeapp.event

import com.mayosen.financeapp.projection.account.AccountSummaryStore
import com.mayosen.financeapp.projection.transaction.TransactionStore
import com.mayosen.financeapp.util.transaction.TransactionManager
import org.apache.logging.log4j.kotlin.Logging
import org.springframework.stereotype.Service

@Service
class ServiceEventProcessor(
    private val accountSummaryStore: AccountSummaryStore,
    private val transactionStore: TransactionStore,
    private val transactionManager: TransactionManager,
) {
    fun process(event: ServiceEvent) {
        when (event) {
            is ResetProjectionsEvent -> processResetProjections(event)
            else -> throw IllegalArgumentException("Unhandled event type: ${event::class.simpleName}")
        }
    }

    fun processResetProjections(event: ResetProjectionsEvent) {
        logger.info { "Deleting projections for accountId='${event.accountId}'. Event: $event" }
        transactionManager.executeInTransaction {
            accountSummaryStore.deleteByAccountId(event.accountId)
            transactionStore.deleteAllByAccountId(event.accountId)
        }
    }

    private companion object : Logging
}
