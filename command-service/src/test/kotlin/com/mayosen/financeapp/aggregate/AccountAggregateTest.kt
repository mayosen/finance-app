package com.mayosen.financeapp.aggregate

import com.mayosen.financeapp.event.AccountCreatedEvent
import com.mayosen.financeapp.event.DepositPerformedEvent
import com.mayosen.financeapp.event.Event
import com.mayosen.financeapp.event.WithdrawalPerformedEvent
import com.mayosen.financeapp.exception.AccountNotFoundException
import com.mayosen.financeapp.snapshot.AccountSnapshot
import com.mayosen.financeapp.test.ACCOUNT_ID
import com.mayosen.financeapp.test.AMOUNT_100
import com.mayosen.financeapp.test.AMOUNT_50
import com.mayosen.financeapp.test.EVENT_ID
import com.mayosen.financeapp.test.LAST_SEQUENCE_NUMBER
import com.mayosen.financeapp.test.OWNER_ID
import com.mayosen.financeapp.test.assertions.isCloseToNow
import com.mayosen.financeapp.test.identifier.TestIdGenerator
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class AccountAggregateTest {
    private lateinit var aggregate: AccountAggregate

    @BeforeEach
    fun setup() {
        aggregate = AccountAggregate(ACCOUNT_ID, TestIdGenerator)
    }

    @Nested
    inner class LoadFromSnapshotTest {
        @Test
        fun `should load from snapshot`() {
            // given
            val snapshot =
                AccountSnapshot(
                    accountId = ACCOUNT_ID,
                    balance = AMOUNT_100,
                    lastSequenceNumber = LAST_SEQUENCE_NUMBER,
                )

            // when
            aggregate.loadFromSnapshot(snapshot)

            // then
            assertThat(aggregate.balance).isEqualTo(AMOUNT_100)
            assertThat(aggregate.getUncommittedEvents()).isEmpty()
        }
    }

    @Nested
    inner class ToSnapshotTest {
        @Test
        fun `should load from snapshot`() {
            // given
            aggregate.createAccount(OWNER_ID)
            aggregate.deposit(AMOUNT_100)

            // when
            val snapshot = aggregate.toSnapshot(LAST_SEQUENCE_NUMBER)

            // then
            assertThat(snapshot.accountId).isEqualTo(ACCOUNT_ID)
            assertThat(snapshot.balance).isEqualTo(AMOUNT_100)
            assertThat(snapshot.lastSequenceNumber).isEqualTo(LAST_SEQUENCE_NUMBER)
            assertThat(snapshot.timestamp).isCloseToNow()
        }
    }

    @Nested
    inner class LoadFromHistoryTest {
        @Test
        fun `should throw exception when event list is empty`() {
            // given
            val events = emptyList<Event>()

            // when
            val exception =
                assertThrows<AccountNotFoundException> {
                    aggregate.loadFromHistory(events)
                }

            assertThat(exception).hasMessageContaining(ACCOUNT_ID)
        }

        @Test
        fun `should load from history`() {
            // given
            val events =
                listOf(
                    AccountCreatedEvent(
                        eventId = EVENT_ID,
                        accountId = ACCOUNT_ID,
                        ownerId = OWNER_ID,
                    ),
                    DepositPerformedEvent(
                        eventId = EVENT_ID,
                        accountId = ACCOUNT_ID,
                        amount = AMOUNT_100,
                    ),
                    WithdrawalPerformedEvent(
                        eventId = EVENT_ID,
                        accountId = ACCOUNT_ID,
                        amount = AMOUNT_50,
                    ),
                )

            // when
            aggregate.loadFromHistory(events)

            // then
            assertThat(aggregate.getUncommittedEvents()).isEmpty()
            assertThat(aggregate.balance).isEqualTo(AMOUNT_50)
        }
    }

    @Nested
    inner class GetUncommittedEventsTest {
        @Test
        fun `should return uncommitted events`() {
            // given
            aggregate.createAccount(OWNER_ID)
            aggregate.deposit(AMOUNT_100)
            aggregate.withdraw(AMOUNT_50)

            // when
            val events = aggregate.getUncommittedEvents()

            // then
            assertThat(events).hasSize(3)
            assertThat(events).hasExactlyElementsOfTypes(
                AccountCreatedEvent::class.java,
                DepositPerformedEvent::class.java,
                WithdrawalPerformedEvent::class.java,
            )
        }
    }
}
