package com.mayosen.financeapp.event

import com.mayosen.financeapp.projection.account.AccountSummaryStore
import com.mayosen.financeapp.projection.transaction.TransactionStore
import org.springframework.stereotype.Service

@Service
class EventDeduplicator(
    private val accountSummaryStore: AccountSummaryStore,
    private val transactionStore: TransactionStore,
) {
    fun hasEventBeenProcessed(event: Event): Boolean =
        when (event) {
            is AccountCreatedEvent,
            is AccountDeletedEvent,
            -> accountSummaryStore.hasBeenUpdatedBy(event)

            is DepositPerformedEvent,
            is WithdrawalPerformedEvent,
            is TransferPerformedEvent,
            -> transactionStore.hasBeenUpdatedBy(event)

            else -> false
        }
}
