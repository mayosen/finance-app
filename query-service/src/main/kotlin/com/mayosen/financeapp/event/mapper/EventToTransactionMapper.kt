package com.mayosen.financeapp.event.mapper

import com.mayosen.financeapp.event.DepositPerformedEvent
import com.mayosen.financeapp.event.Event
import com.mayosen.financeapp.event.TransferPerformedEvent
import com.mayosen.financeapp.event.WithdrawalPerformedEvent
import com.mayosen.financeapp.projection.transaction.Transaction
import com.mayosen.financeapp.projection.transaction.TransactionType
import com.mayosen.financeapp.util.identifier.IdGenerator
import org.springframework.stereotype.Component

@Component
class EventToTransactionMapper(
    private val idGenerator: IdGenerator,
) {
    fun toTransactions(event: Event): List<Transaction> =
        when (event) {
            is DepositPerformedEvent -> listOf(event.toTransaction())
            is WithdrawalPerformedEvent -> listOf(event.toTransaction())
            is TransferPerformedEvent ->
                listOf(
                    event.toSourceTransaction(),
                    event.toDestinationTransaction(),
                )

            else -> throw IllegalArgumentException("Unexpected event type: ${event::class}")
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

    private fun TransferPerformedEvent.toSourceTransaction(): Transaction =
        Transaction(
            accountId = accountId,
            sourceEventId = eventId,
            transactionId = idGenerator.generateTransactionId(),
            type = TransactionType.TRANSFER_OUT,
            amount = amount,
            timestamp = timestamp,
            relatedAccountId = toAccountId,
        )

    private fun TransferPerformedEvent.toDestinationTransaction(): Transaction =
        Transaction(
            accountId = toAccountId,
            sourceEventId = eventId,
            transactionId = idGenerator.generateTransactionId(),
            type = TransactionType.TRANSFER_IN,
            amount = amount,
            timestamp = timestamp,
            relatedAccountId = accountId,
        )
}
