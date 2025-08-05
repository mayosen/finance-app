package com.mayosen.financeapp.event

import com.mayosen.financeapp.event.mapper.EventToTransactionMapper
import com.mayosen.financeapp.projection.account.AccountSummary
import com.mayosen.financeapp.projection.account.AccountSummaryStore
import com.mayosen.financeapp.projection.transaction.TransactionStore
import com.mayosen.financeapp.util.transaction.TransactionManager
import org.apache.logging.log4j.kotlin.Logging
import org.springframework.stereotype.Service
import java.math.BigDecimal

/**
 * Обновляет Read Model на основе событий.
 */
@Service
class AccountProjector(
    private val accountSummaryStore: AccountSummaryStore,
    private val transactionStore: TransactionStore,
    private val transactionManager: TransactionManager,
    private val eventToTransactionMapper: EventToTransactionMapper,
    private val eventDeduplicator: EventDeduplicator,
) {
    fun project(event: Event) {
        if (eventDeduplicator.hasEventBeenProcessed(event)) {
            logger.info { "Event '${event.accountId}' has already been processed. Skipping changes" }
            return
        }

        when (event) {
            is AccountCreatedEvent -> applyAccountCreated(event)
            is DepositPerformedEvent -> applyDepositPerformed(event)
            is WithdrawalPerformedEvent -> applyWithdrawalPerformed(event)
            is TransferPerformedEvent -> applyTransferPerformed(event)
            is AccountDeletedEvent -> applyAccountDeleted(event)
            else -> error("Unhandled event type: ${event::class.simpleName}")
        }
    }

    private fun applyAccountCreated(event: AccountCreatedEvent) {
        logger.info { "Creating account summary. Event: $event" }
        val summary =
            AccountSummary(
                accountId = event.accountId,
                balance = BigDecimal.ZERO,
                ownerId = event.ownerId,
                updatedAt = event.timestamp,
                sourceEventId = event.eventId,
            )
        accountSummaryStore.save(summary)
    }

    private fun applyDepositPerformed(event: DepositPerformedEvent) {
        logger.info { "Updating account summary. Adding transaction. Event: $event" }
        val summary =
            accountSummaryStore.findByAccountId(event.accountId)
                ?: throw IllegalStateException("Account not found: ${event.accountId}")
        val updated =
            summary.copy(
                balance = summary.balance + event.amount,
                updatedAt = event.timestamp,
                sourceEventId = event.eventId,
            )
        val transactions = eventToTransactionMapper.toTransactions(event)

        transactionManager.executeInTransaction {
            accountSummaryStore.save(updated)
            transactionStore.saveAll(transactions)
        }
    }

    private fun applyWithdrawalPerformed(event: WithdrawalPerformedEvent) {
        logger.info { "Updating account summary. Adding transaction. Event: $event" }
        val summary =
            accountSummaryStore.findByAccountId(event.accountId)
                ?: throw IllegalStateException("Account not found: ${event.accountId}")
        val updated =
            summary.copy(
                balance = summary.balance - event.amount,
                updatedAt = event.timestamp,
                sourceEventId = event.eventId,
            )
        val transactions = eventToTransactionMapper.toTransactions(event)

        transactionManager.executeInTransaction {
            accountSummaryStore.save(updated)
            transactionStore.saveAll(transactions)
        }
    }

    private fun applyTransferPerformed(event: TransferPerformedEvent) {
        logger.info { "Updating accounts summary. Adding transactions to accounts. Event: $event" }
        val source =
            accountSummaryStore.findByAccountId(event.accountId)
                ?: throw IllegalStateException("Source account not found: ${event.accountId}")
        val destination =
            accountSummaryStore.findByAccountId(event.toAccountId)
                ?: throw IllegalStateException("Destination account not found: ${event.accountId}")

        val updatedSource =
            source.copy(
                balance = source.balance - event.amount,
                updatedAt = event.timestamp,
                sourceEventId = event.eventId,
            )
        val updatedDestination =
            destination.copy(
                balance = destination.balance + event.amount,
                updatedAt = event.timestamp,
                sourceEventId = event.eventId,
            )

        val transactions = eventToTransactionMapper.toTransactions(event)

        transactionManager.executeInTransaction {
            accountSummaryStore.saveAll(listOf(updatedSource, updatedDestination))
            transactionStore.saveAll(transactions)
        }
    }

    private fun applyAccountDeleted(event: AccountDeletedEvent) {
        logger.info { "Deleting account summary, deleting transactions. Event: $event" }

        transactionManager.executeInTransaction {
            accountSummaryStore.deleteByAccountId(event.accountId)
            transactionStore.deleteAllByAccountId(event.accountId)
        }
    }

    private companion object : Logging
}
