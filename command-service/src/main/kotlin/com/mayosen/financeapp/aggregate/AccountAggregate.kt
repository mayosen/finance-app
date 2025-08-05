package com.mayosen.financeapp.aggregate

import com.mayosen.financeapp.event.AccountCreatedEvent
import com.mayosen.financeapp.event.AccountDeletedEvent
import com.mayosen.financeapp.event.DepositPerformedEvent
import com.mayosen.financeapp.event.Event
import com.mayosen.financeapp.event.TransferPerformedEvent
import com.mayosen.financeapp.event.WithdrawalPerformedEvent
import com.mayosen.financeapp.exception.AccountNotFoundException
import com.mayosen.financeapp.snapshot.AccountSnapshot
import com.mayosen.financeapp.util.identifier.IdGenerator
import java.math.BigDecimal

/**
 * Агрегат Account — инкапсулирует бизнес-логику и состояние счёта.
 * Применяет события, проверяет инварианты, генерирует новые события.
 */
class AccountAggregate(
    val accountId: String,
    private val idGenerator: IdGenerator,
) {
    var balance: BigDecimal = BigDecimal.ZERO

    var created: Boolean = false

    private val newEvents = mutableListOf<Event>()

    fun loadFromSnapshot(snapshot: AccountSnapshot) {
        require(snapshot.accountId == accountId) { "Snapshot ID does not match aggregate ID" }
        this.balance = snapshot.balance
        this.created = true
    }

    fun toSnapshot(lastSequenceNumber: Long): AccountSnapshot =
        AccountSnapshot(
            accountId = accountId,
            balance = balance,
            lastSequenceNumber = lastSequenceNumber,
        )

    /**
     * Применяет историю событий для восстановления текущего состояния
     * */
    fun loadFromHistory(events: List<Event>) {
        if (events.isEmpty()) {
            throw AccountNotFoundException(accountId)
        }
        events.forEach {
            applyEventAndStore(it, isNew = false)
        }
    }

    /**
     * Возвращает список новых событий, сгенерированных агрегатом
     * */
    fun getUncommittedEvents(): List<Event> = newEvents.toList()

    fun markEventsCommitted() {
        newEvents.clear()
    }

    fun createAccount(ownerId: String) {
        if (created) {
            throw IllegalStateException("Account already created")
        }
        val event =
            AccountCreatedEvent(
                eventId = idGenerator.generateEventId(),
                accountId = accountId,
                ownerId = ownerId,
            )
        applyEventAndStore(event)
    }

    fun deposit(amount: BigDecimal) {
        require(amount > BigDecimal.ZERO) { "Deposit amount must be positive" }
        val event =
            DepositPerformedEvent(
                eventId = idGenerator.generateEventId(),
                accountId = accountId,
                amount = amount,
            )
        applyEventAndStore(event)
    }

    fun withdraw(amount: BigDecimal) {
        require(amount > BigDecimal.ZERO) { "Withdrawal amount must be positive" }
        if (balance < amount) {
            throw IllegalStateException("Insufficient funds")
        }
        val event =
            WithdrawalPerformedEvent(
                eventId = idGenerator.generateEventId(),
                accountId = accountId,
                amount = amount,
            )
        applyEventAndStore(event)
    }

    fun transfer(
        toAccountId: String,
        amount: BigDecimal,
    ) {
        require(amount > BigDecimal.ZERO) { "Transfer amount must be positive" }
        if (balance < amount) {
            throw IllegalStateException("Insufficient funds")
        }
        val event =
            TransferPerformedEvent(
                eventId = idGenerator.generateEventId(),
                accountId = accountId,
                toAccountId = toAccountId,
                amount = amount,
            )
        applyEventAndStore(event)
    }

    fun deleteAccount() {
        if (!created) {
            throw IllegalStateException("Account is not created")
        }
        val event =
            AccountDeletedEvent(
                eventId = idGenerator.generateEventId(),
                accountId = accountId,
            )
        applyEventAndStore(event)
    }

    /**
     * Применение события: изменяет состояние + сохраняет его, если оно новое.
     * */
    private fun applyEventAndStore(
        event: Event,
        isNew: Boolean = true,
    ) {
        applyEvent(event)
        if (isNew) {
            newEvents += event
        }
    }

    private fun applyEvent(event: Event) {
        when (event) {
            is AccountCreatedEvent -> {
                balance = BigDecimal.ZERO
                created = true
            }

            is DepositPerformedEvent -> balance += event.amount
            is WithdrawalPerformedEvent -> balance -= event.amount
            is TransferPerformedEvent -> balance -= event.amount
            is AccountDeletedEvent -> created = false
            else -> throw IllegalArgumentException("Unexpected event: ${event::class.simpleName}")
        }
    }
}
