package com.mayosen.financeapp.aggregate

import com.mayosen.financeapp.event.AccountCreatedEvent
import com.mayosen.financeapp.event.DepositPerformedEvent
import com.mayosen.financeapp.event.Event
import com.mayosen.financeapp.event.WithdrawalPerformedEvent
import com.mayosen.financeapp.exception.AccountNotFoundException
import com.mayosen.financeapp.snapshot.AccountSnapshot
import com.mayosen.financeapp.test.ACCOUNT_ID
import com.mayosen.financeapp.test.BALANCE_100
import com.mayosen.financeapp.test.BALANCE_50
import com.mayosen.financeapp.test.EVENT_ID
import com.mayosen.financeapp.test.LAST_SEQUENCE_NUMBER
import com.mayosen.financeapp.test.OWNER_ID
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.byLessThan
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.Instant
import java.time.temporal.ChronoUnit

// TODO: Use @DisplayName
// TODO: Add generators

class AccountAggregateTest {
    private lateinit var aggregate: AccountAggregate

    @BeforeEach
    fun setup() {
        aggregate = AccountAggregate(ACCOUNT_ID)
    }

    @Nested
    inner class LoadFromSnapshotTest {
        @Test
        fun `should load from snapshot`() {
            // given
            val snapshot =
                AccountSnapshot(
                    accountId = ACCOUNT_ID,
                    balance = BALANCE_100,
                    created = true,
                    lastSequenceNumber = LAST_SEQUENCE_NUMBER,
                )

            // when
            aggregate.loadFromSnapshot(snapshot)

            // then
            assertThat(aggregate.balance).isEqualTo(BALANCE_100)
            assertThat(aggregate.getUncommittedEvents()).isEmpty()
        }
    }

    @Nested
    inner class ToSnapshotTest {
        @Test
        fun `should load from snapshot`() {
            // given
            aggregate.createAccount(OWNER_ID)
            aggregate.deposit(BALANCE_100)

            // when
            val snapshot = aggregate.toSnapshot(LAST_SEQUENCE_NUMBER)

            // then
            assertThat(snapshot.accountId).isEqualTo(ACCOUNT_ID)
            assertThat(snapshot.balance).isEqualTo(BALANCE_100)
            assertThat(snapshot.created).isTrue()
            assertThat(snapshot.lastSequenceNumber).isEqualTo(LAST_SEQUENCE_NUMBER)
            assertThat(snapshot.timestamp).isCloseTo(Instant.now(), byLessThan(1, ChronoUnit.SECONDS))
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
                        aggregateId = ACCOUNT_ID,
                        ownerId = OWNER_ID,
                    ),
                    DepositPerformedEvent(
                        eventId = EVENT_ID,
                        aggregateId = ACCOUNT_ID,
                        amount = BALANCE_100,
                    ),
                    WithdrawalPerformedEvent(
                        eventId = EVENT_ID,
                        aggregateId = ACCOUNT_ID,
                        amount = BALANCE_50,
                    ),
                )

            // when
            aggregate.loadFromHistory(events)

            // then
            assertThat(aggregate.getUncommittedEvents()).isEmpty()
            assertThat(aggregate.balance).isEqualTo(BALANCE_50)
        }
    }

    @Nested
    inner class GetUncommittedEventsTest {
        @Test
        fun `should return uncommitted events`() {
            // given
            aggregate.createAccount(OWNER_ID)
            aggregate.deposit(BALANCE_100)
            aggregate.withdraw(BALANCE_50)

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
