package com.mayosen.financeapp.event

import com.mayosen.financeapp.projection.account.AccountSummary
import com.mayosen.financeapp.projection.account.AccountSummaryStore
import com.mayosen.financeapp.projection.transaction.Transaction
import com.mayosen.financeapp.projection.transaction.TransactionStore
import com.mayosen.financeapp.projection.transaction.TransactionType
import com.mayosen.financeapp.util.IdGenerator
import org.apache.logging.log4j.kotlin.Logging
import org.springframework.stereotype.Service
import org.springframework.transaction.support.TransactionTemplate
import java.math.BigDecimal

// TODO: Здесь и в других местах групповые изменения выполнять в транзакции. TransactionTemplate, например

/**
 * Обновляет Read Model на основе событий.
 */
@Service
class AccountProjector(
    private val accountSummaryStore: AccountSummaryStore,
    private val transactionStore: TransactionStore,
    private val transactionTemplate: TransactionTemplate,
    private val idGenerator: IdGenerator,
) {
    fun project(event: Event) {
        // TODO: Check if event is already applied in read model. Do not process it twice.
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
        accountSummaryStore.save(updated)
        val transaction = event.toTransaction()
        transactionStore.save(transaction)
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
        accountSummaryStore.save(updated)
        val transaction = event.toTransaction()
        transactionStore.save(transaction)
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

        accountSummaryStore.saveAll(listOf(updatedSource, updatedDestination))

        val sourceTransaction = event.toSourceTransaction()
        val destinationTransaction = event.toDestinationTransaction()
        transactionStore.saveAll(listOf(sourceTransaction, destinationTransaction))
    }

    private fun DepositPerformedEvent.toTransaction(): Transaction =
        Transaction(
            accountId = accountId,
            sourceEventId = eventId,
            transactionId = idGenerator.generateTransactionId(),
            type = TransactionType.DEPOSIT,
            amount = amount,
            timestamp = timestamp,
            relatedAccountId = null,
        )

    private fun WithdrawalPerformedEvent.toTransaction(): Transaction =
        Transaction(
            accountId = accountId,
            sourceEventId = eventId,
            transactionId = idGenerator.generateTransactionId(),
            type = TransactionType.WITHDRAWAL,
            amount = amount,
            timestamp = timestamp,
            relatedAccountId = null,
        )

    @Suppress("UNNECESSARY_NOT_NULL_ASSERTION")
    private fun TransferPerformedEvent.toSourceTransaction(): Transaction =
        Transaction(
            accountId = accountId,
            sourceEventId = eventId,
            transactionId = idGenerator.generateTransactionId(),
            type = TransactionType.TRANSFER_OUT,
            amount = amount,
            timestamp = timestamp,
            relatedAccountId = toAccountId!!,
        )

    @Suppress("UNNECESSARY_NOT_NULL_ASSERTION")
    private fun TransferPerformedEvent.toDestinationTransaction(): Transaction =
        Transaction(
            accountId = toAccountId,
            sourceEventId = eventId,
            transactionId = idGenerator.generateTransactionId(),
            type = TransactionType.TRANSFER_IN,
            amount = amount,
            timestamp = timestamp,
            relatedAccountId = accountId!!,
        )

    private fun applyAccountDeleted(event: AccountDeletedEvent) {
        logger.info { "Deleting account summary, deleting transactions. Event: $event" }

        transactionTemplate.executeWithoutResult {
            accountSummaryStore.deleteByAccountId(event.accountId)
            transactionStore.deleteAllByAccountId(event.accountId)
        }
    }

    private companion object : Logging
}
