package com.mayosen.financeapp.integration.event

import com.mayosen.financeapp.event.TransferPerformedEvent
import com.mayosen.financeapp.projection.account.jdbc.AccountSummaryEntity
import com.mayosen.financeapp.projection.transaction.jdbc.TransactionEntity
import com.mayosen.financeapp.projection.transaction.jdbc.TransactionEntity.TransactionType
import com.mayosen.financeapp.test.ACCOUNT_ID
import com.mayosen.financeapp.test.ACCOUNT_ID_2
import com.mayosen.financeapp.test.AMOUNT_0
import com.mayosen.financeapp.test.AMOUNT_100
import com.mayosen.financeapp.test.AMOUNT_50
import com.mayosen.financeapp.test.EVENT_ID_3
import com.mayosen.financeapp.test.INSTANT
import com.mayosen.financeapp.test.OWNER_ID
import com.mayosen.financeapp.test.TRANSACTION_ID
import com.mayosen.financeapp.test.TRANSACTION_ID_2
import com.mayosen.financeapp.test.assertions.isCloseToNow
import com.mayosen.financeapp.test.context.BaseIntegrationTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ProjectTransferPerformedEventIT : BaseIntegrationTest() {
    @Test
    fun `happy path - should project TransferPerformedEvent`() {
        // given: account summary 1 saved
        var summaryEntity =
            AccountSummaryEntity(
                accountId = ACCOUNT_ID,
                ownerId = OWNER_ID,
                balance = AMOUNT_100,
                updatedAt = INSTANT,
                sourceEventId = idGenerator.generateEventId(),
                isNewEntity = true,
            )
        jdbcTemplate.save(summaryEntity)

        // given: account summary 2 saved
        summaryEntity =
            AccountSummaryEntity(
                accountId = ACCOUNT_ID_2,
                ownerId = OWNER_ID,
                balance = AMOUNT_0,
                updatedAt = INSTANT,
                sourceEventId = idGenerator.generateEventId(),
                isNewEntity = true,
            )
        jdbcTemplate.save(summaryEntity)

        // given
        val event =
            TransferPerformedEvent(
                eventId = idGenerator.generateEventId(),
                accountId = ACCOUNT_ID,
                toAccountId = ACCOUNT_ID_2,
                amount = AMOUNT_50,
            )

        // step: publish
        eventKafkaTemplate.send(event.toProducerRecord())

        // step: verify account summary 1 updated
        baseAwait {
            val summaries = jdbcTemplate.findAll(AccountSummaryEntity::class.java)
            assertThat(summaries).hasSize(2)

            val summary1 = summaries.first { it.accountId == ACCOUNT_ID }
            assertThat(summary1.accountId).isEqualTo(ACCOUNT_ID)
            assertThat(summary1.ownerId).isEqualTo(OWNER_ID)
            assertThat(summary1.balance).isEqualTo(AMOUNT_50)
            assertThat(summary1.updatedAt).isCloseToNow()
            assertThat(summary1.sourceEventId).isEqualTo(EVENT_ID_3)
            assertThat(summary1.isNewEntity).isFalse()
        }

        // step: verify account summary 2 updated
        val summary2 = jdbcTemplate.findById(ACCOUNT_ID_2, AccountSummaryEntity::class.java)
        assertThat(summary2).isNotNull()

        assertThat(summary2.accountId).isEqualTo(ACCOUNT_ID_2)
        assertThat(summary2.ownerId).isEqualTo(OWNER_ID)
        assertThat(summary2.balance).isEqualTo(AMOUNT_50)
        assertThat(summary2.updatedAt).isCloseToNow()
        assertThat(summary2.sourceEventId).isEqualTo(EVENT_ID_3)
        assertThat(summary2.isNewEntity).isFalse()

        // step: verify transaction 1 created
        val transactions = jdbcTemplate.findAll(TransactionEntity::class.java)
        assertThat(transactions).hasSize(2)

        val transaction1 = transactions.first { it.accountId == ACCOUNT_ID }
        assertThat(transaction1.transactionId).isEqualTo(TRANSACTION_ID)
        assertThat(transaction1.accountId).isEqualTo(ACCOUNT_ID)
        assertThat(transaction1.sourceEventId).isEqualTo(EVENT_ID_3)
        assertThat(transaction1.type).isEqualTo(TransactionType.TRANSFER_OUT)
        assertThat(transaction1.amount).isEqualTo(AMOUNT_50)
        assertThat(transaction1.timestamp).isCloseToNow()
        assertThat(transaction1.relatedAccountId).isEqualTo(ACCOUNT_ID_2)
        assertThat(transaction1.isNewEntity).isFalse()

        // step: verify transaction 2 created
        val transaction2 = transactions.first { it.accountId == ACCOUNT_ID_2 }
        assertThat(transaction2.transactionId).isEqualTo(TRANSACTION_ID_2)
        assertThat(transaction2.accountId).isEqualTo(ACCOUNT_ID_2)
        assertThat(transaction2.sourceEventId).isEqualTo(EVENT_ID_3)
        assertThat(transaction2.type).isEqualTo(TransactionType.TRANSFER_IN)
        assertThat(transaction2.amount).isEqualTo(AMOUNT_50)
        assertThat(transaction2.timestamp).isCloseToNow()
        assertThat(transaction2.relatedAccountId).isEqualTo(ACCOUNT_ID)
        assertThat(transaction2.isNewEntity).isFalse()
    }
}
