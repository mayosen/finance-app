package com.mayosen.financeapp.command

import com.mayosen.financeapp.aggregate.AccountAggregate
import com.mayosen.financeapp.command.api.CreateAccountCommand
import com.mayosen.financeapp.command.api.DeleteAccountCommand
import com.mayosen.financeapp.command.api.DepositCommand
import com.mayosen.financeapp.command.api.TransferCommand
import com.mayosen.financeapp.command.api.WithdrawCommand
import com.mayosen.financeapp.event.AccountCreatedEvent
import com.mayosen.financeapp.event.AccountDeletedEvent
import com.mayosen.financeapp.event.Event
import com.mayosen.financeapp.event.EventPublisher
import com.mayosen.financeapp.event.EventStore
import com.mayosen.financeapp.exception.EventProcessingException
import com.mayosen.financeapp.snapshot.CreateSnapshotStrategy
import com.mayosen.financeapp.snapshot.SnapshotStore
import com.mayosen.financeapp.util.IdGenerator.generateAccountId
import org.apache.logging.log4j.kotlin.Logging
import org.springframework.stereotype.Service

/**
 * Обрабатывает команды, делегируя логику агрегату.
 */
@Service
class CommandHandler(
    private val eventStore: EventStore,
    private val snapshotStore: SnapshotStore,
    private val eventPublisher: EventPublisher,
    private val createSnapshotStrategy: CreateSnapshotStrategy,
) {
    fun handleCreateAccount(command: CreateAccountCommand) {
        logger.info { "Processing CreateAccountCommand: $command" }

        val accountId = generateAccountId()
        val aggregate = AccountAggregate(accountId)

        aggregate.createAccount(command.ownerId)

        persistAndPublish(aggregate)
    }

    fun handleDeposit(command: DepositCommand) {
        logger.info { "Processing DepositCommand: $command" }

        val aggregate = loadAggregate(command.accountId)
        aggregate.deposit(command.amount)

        persistAndPublish(aggregate)
    }

    fun handleWithdraw(command: WithdrawCommand) {
        logger.info { "Processing WithdrawCommand: $command" }

        val aggregate = loadAggregate(command.accountId)
        aggregate.withdraw(command.amount)

        persistAndPublish(aggregate)
    }

    fun handleTransfer(command: TransferCommand) {
        logger.info { "Processing TransferCommand: $command" }

        val aggregate = loadAggregate(command.fromAccountId)
        aggregate.transfer(command.toAccountId, command.amount)

        persistAndPublish(aggregate)
    }

    private fun loadAggregate(accountId: String): AccountAggregate {
        val snapshot = snapshotStore.findByAggregateId(accountId)
        val aggregate = AccountAggregate(accountId)
        val events: List<Event>

        if (snapshot != null) {
            aggregate.loadFromSnapshot(snapshot)
            events = eventStore.findAllByAggregateIdAfterSequenceNumber(accountId, snapshot.lastSequenceNumber)
        } else {
            events = eventStore.findAllByAggregateId(accountId)
        }

        aggregate.loadFromHistory(events)
        return aggregate
    }

    fun handleDeleteAccount(command: DeleteAccountCommand) {
        logger.info { "Processing DeleteAccountCommand: $command" }

        val aggregate = AccountAggregate(command.accountId)

        if (aggregate.exists()) {
            aggregate.deleteAccount()
            persistAndPublish(aggregate)
            snapshotStore.delete(aggregate)
        }
    }

    private fun AccountAggregate.exists(): Boolean {
        val snapshotExists = snapshotStore.findByAggregateId(accountId) != null
        if (snapshotExists) {
            return true
        }
        val accountEvents =
            eventStore.findAllByAggregateIdAndTypeIn(
                aggregateId = accountId,
                types = listOf(AccountCreatedEvent::class, AccountDeletedEvent::class),
            )
        val accountHasBeenCreated = accountEvents.count { it is AccountCreatedEvent } == 1
        val accountHasNotBeenDeleted = accountEvents.count { it is AccountDeletedEvent } == 0
        return accountHasBeenCreated && accountHasNotBeenDeleted
    }

    private fun persistAndPublish(aggregate: AccountAggregate) {
        val newEvents = aggregate.getUncommittedEvents()
        if (newEvents.isEmpty()) {
            logger.debug("Empty events list. Skipping updates")
            return
        }

        // TODO: In transaction
        try {
            eventStore.saveAll(newEvents)
            eventPublisher.publishAll(newEvents)

            if (createSnapshotStrategy.shouldCreateSnapshot(aggregate)) {
                snapshotStore.save(aggregate)
            }

            logger.info { "Successfully processed events for aggregate ${aggregate.accountId}" }
            aggregate.markEventsCommitted()
        } catch (ex: Exception) {
            logger.error(ex) { "Failed to process events for aggregate ${aggregate.accountId}" }
            throw EventProcessingException("Failed to process events", ex)
        }
    }

    private companion object : Logging
}
