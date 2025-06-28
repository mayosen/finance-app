package com.mayosen.financeapp.integration.event

import com.mayosen.financeapp.event.WithdrawalPerformedEvent
import com.mayosen.financeapp.projection.account.jdbc.AccountSummaryEntity
import com.mayosen.financeapp.projection.transaction.jdbc.TransactionEntity
import com.mayosen.financeapp.projection.transaction.jdbc.TransactionEntity.TransactionType
import com.mayosen.financeapp.test.ACCOUNT_ID
import com.mayosen.financeapp.test.AMOUNT_0
import com.mayosen.financeapp.test.AMOUNT_50
import com.mayosen.financeapp.test.EVENT_ID_2
import com.mayosen.financeapp.test.INSTANT
import com.mayosen.financeapp.test.OWNER_ID
import com.mayosen.financeapp.test.TRANSACTION_ID
import com.mayosen.financeapp.test.assertions.isCloseToNow
import com.mayosen.financeapp.test.context.BaseIntegrationTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ProjectWithdrawPerformedEventIT : BaseIntegrationTest() {
    @Test
    fun `happy path - should project WithdrawalPerformedEvent`() {
        // given: account summary saved
        val entity =
            AccountSummaryEntity(
                accountId = ACCOUNT_ID,
                ownerId = OWNER_ID,
                balance = AMOUNT_50,
                updatedAt = INSTANT,
                sourceEventId = idGenerator.generateEventId(),
                isNewEntity = true,
            )
        jdbcTemplate.save(entity)

        // given
        val event =
            WithdrawalPerformedEvent(
                eventId = idGenerator.generateEventId(),
                accountId = ACCOUNT_ID,
                amount = AMOUNT_50,
            )

        // step: publish
        eventKafkaTemplate.send(event.toProducerRecord())

        // step: verify account summary updated
        baseAwait {
            val summaries = jdbcTemplate.findAll(AccountSummaryEntity::class.java)
            assertThat(summaries).hasSize(1)

            val summary = summaries.first()
            assertThat(summary.accountId).isEqualTo(ACCOUNT_ID)
            assertThat(summary.ownerId).isEqualTo(OWNER_ID)
            assertThat(summary.balance).isEqualTo(AMOUNT_0)
            assertThat(summary.updatedAt).isCloseToNow()
            assertThat(summary.sourceEventId).isEqualTo(EVENT_ID_2)
            assertThat(summary.isNewEntity).isFalse()
        }

        // step: verify transaction created
        val transactions = jdbcTemplate.findAll(TransactionEntity::class.java)
        assertThat(transactions).hasSize(1)

        val transaction = transactions.first()
        assertThat(transaction.transactionId).isEqualTo(TRANSACTION_ID)
        assertThat(transaction.accountId).isEqualTo(ACCOUNT_ID)
        assertThat(transaction.sourceEventId).isEqualTo(EVENT_ID_2)
        assertThat(transaction.type).isEqualTo(TransactionType.WITHDRAWAL)
        assertThat(transaction.amount).isEqualTo(AMOUNT_50)
        assertThat(transaction.timestamp).isCloseToNow()
        assertThat(transaction.relatedAccountId).isNull()
        assertThat(transaction.isNewEntity).isFalse()
    }
}
