package com.mayosen.financeapp.integration.event

import com.mayosen.financeapp.event.AccountCreatedEvent
import com.mayosen.financeapp.event.DepositPerformedEvent
import com.mayosen.financeapp.event.ResetProjectionsEvent
import com.mayosen.financeapp.projection.account.jdbc.AccountSummaryEntity
import com.mayosen.financeapp.projection.transaction.jdbc.TransactionEntity
import com.mayosen.financeapp.projection.transaction.jdbc.TransactionEntity.TransactionType
import com.mayosen.financeapp.test.ACCOUNT_ID
import com.mayosen.financeapp.test.AMOUNT_0
import com.mayosen.financeapp.test.AMOUNT_50
import com.mayosen.financeapp.test.EVENT_ID
import com.mayosen.financeapp.test.EVENT_ID_2
import com.mayosen.financeapp.test.EVENT_ID_3
import com.mayosen.financeapp.test.INSTANT
import com.mayosen.financeapp.test.OWNER_ID
import com.mayosen.financeapp.test.TRANSACTION_ID
import com.mayosen.financeapp.test.context.BaseIntegrationTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ProjectResetProjectionsEventIT : BaseIntegrationTest() {
    @Test
    fun `happy path - should project ResetProjectionsEvent`() {
        // given: outdated account summary saved
        val summaryEntity =
            AccountSummaryEntity(
                accountId = ACCOUNT_ID,
                ownerId = OWNER_ID,
                balance = AMOUNT_0,
                updatedAt = INSTANT,
                sourceEventId = EVENT_ID,
                isNewEntity = true,
            )
        jdbcTemplate.save(summaryEntity)

        // given: outdated transactions saved
        val transferIn =
            TransactionEntity(
                transactionId = TRANSACTION_ID,
                accountId = ACCOUNT_ID,
                sourceEventId = EVENT_ID,
                type = TransactionType.DEPOSIT,
                amount = AMOUNT_50,
                timestamp = INSTANT,
                relatedAccountId = null,
                isNewEntity = true,
            )
        jdbcTemplate.save(transferIn)

        // given
        val resetProjectionsEvent =
            ResetProjectionsEvent(
                eventId = EVENT_ID_3,
                accountId = ACCOUNT_ID,
            )
        val accountCreatedEvent =
            AccountCreatedEvent(
                eventId = EVENT_ID,
                accountId = ACCOUNT_ID,
                timestamp = INSTANT,
                ownerId = OWNER_ID,
            )
        val depositPerformedEvent =
            DepositPerformedEvent(
                eventId = EVENT_ID_2,
                accountId = ACCOUNT_ID,
                amount = AMOUNT_50,
                timestamp = INSTANT,
            )

        // step: publish events
        listOf(resetProjectionsEvent, accountCreatedEvent, depositPerformedEvent)
            .forEach { eventKafkaTemplate.send(it.toProducerRecord()) }

        // step: verify account summary saved
        baseAwait {
            val summaries = jdbcTemplate.findAll(AccountSummaryEntity::class.java)
            assertThat(summaries).hasSize(1)

            val summary = summaries.first()
            assertThat(summary.accountId).isEqualTo(ACCOUNT_ID)
            assertThat(summary.ownerId).isEqualTo(OWNER_ID)
            assertThat(summary.balance).isEqualTo(AMOUNT_50)
            assertThat(summary.updatedAt).isEqualTo(INSTANT)
            assertThat(summary.sourceEventId).isEqualTo(EVENT_ID_2)
            assertThat(summary.isNewEntity).isFalse()
        }

        // step: verify transaction saved
        val transactions = jdbcTemplate.findAll(TransactionEntity::class.java)
        assertThat(transactions).hasSize(1)

        val transaction = transactions.first()
        assertThat(transaction.transactionId).isEqualTo(TRANSACTION_ID)
        assertThat(transaction.accountId).isEqualTo(ACCOUNT_ID)
        assertThat(transaction.sourceEventId).isEqualTo(EVENT_ID_2)
        assertThat(transaction.type).isEqualTo(TransactionType.DEPOSIT)
        assertThat(transaction.amount).isEqualTo(AMOUNT_50)
        assertThat(transaction.timestamp).isEqualTo(INSTANT)
        assertThat(transaction.relatedAccountId).isNull()
        assertThat(transaction.isNewEntity).isFalse()
    }
}
