package com.mayosen.financeapp.event

import com.mayosen.financeapp.projection.account.AccountSummaryStore
import com.mayosen.financeapp.projection.transaction.TransactionStore
import org.apache.logging.log4j.kotlin.Logging
import org.springframework.stereotype.Service
import org.springframework.transaction.support.TransactionTemplate

@Service
class ServiceEventProcessor(
    private val accountSummaryStore: AccountSummaryStore,
    private val transactionStore: TransactionStore,
    private val transactionTemplate: TransactionTemplate,
) {
    fun process(event: ServiceEvent) {
        when (event) {
            is ResetProjectionsEvent -> processResetProjections(event)
            else -> error("Unhandled event type: ${event::class.simpleName}")
        }
    }

    fun processResetProjections(event: ResetProjectionsEvent) {
        // TODO: Block rows
        logger.info { "Deleting projections for accountId='${event.accountId}'. Event: $event" }
        transactionTemplate.executeWithoutResult {
            accountSummaryStore.deleteByAccountId(event.accountId)
            transactionStore.deleteAllByAccountId(event.accountId)
        }
    }

    private companion object : Logging
}
