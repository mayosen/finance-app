package com.mayosen.financeapp.event

import com.mayosen.financeapp.readmodel.accountsummary.AccountSummaryStore
import com.mayosen.financeapp.readmodel.transactionhistory.TransactionHistoryStore
import org.apache.logging.log4j.kotlin.Logging
import org.springframework.stereotype.Service
import org.springframework.transaction.support.TransactionTemplate

@Service
class ServiceEventProcessor(
    private val accountSummaryStore: AccountSummaryStore,
    private val transactionHistoryStore: TransactionHistoryStore,
    private val transactionTemplate: TransactionTemplate,
) {
    fun process(event: ServiceEvent) {
        when (event) {
            is ResetReadModelEvent -> processResetReadModel(event)
            else -> error("Unhandled event type: ${event::class.simpleName}")
        }
    }

    fun processResetReadModel(event: ResetReadModelEvent) {
        // TODO: Block rows
        logger.info { "Deleting projections for accountId='${event.accountId}'. Event: $event" }
        transactionTemplate.executeWithoutResult {
            accountSummaryStore.deleteByAccountId(event.accountId)
            transactionHistoryStore.deleteAllByAccountId(event.accountId)
        }
    }

    private companion object : Logging
}
