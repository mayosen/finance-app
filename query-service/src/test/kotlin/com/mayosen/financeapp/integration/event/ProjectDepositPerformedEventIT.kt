package com.mayosen.financeapp.integration.event

import com.mayosen.financeapp.event.DepositPerformedEvent
import com.mayosen.financeapp.projection.account.jdbc.AccountSummaryEntity
import com.mayosen.financeapp.projection.transaction.jdbc.TransactionEntity
import com.mayosen.financeapp.projection.transaction.jdbc.TransactionEntity.TransactionType
import com.mayosen.financeapp.test.ACCOUNT_ID
import com.mayosen.financeapp.test.AMOUNT_100
import com.mayosen.financeapp.test.AMOUNT_50
import com.mayosen.financeapp.test.EVENT_ID_2
import com.mayosen.financeapp.test.INSTANT
import com.mayosen.financeapp.test.INSTANT_2
import com.mayosen.financeapp.test.OWNER_ID
import com.mayosen.financeapp.test.TRANSACTION_ID
import com.mayosen.financeapp.test.assertions.isCloseToNow
import com.mayosen.financeapp.test.context.BaseIntegrationTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ProjectDepositPerformedEventIT : BaseIntegrationTest() {
    @Test
    fun `happy path - should project DepositPerformedEvent`() {
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
            DepositPerformedEvent(
                eventId = idGenerator.generateEventId(),
                accountId = ACCOUNT_ID,
                amount = AMOUNT_50,
            )

        // step: publish
        eventKafkaTemplate.send(event.toProducerRecord())

        // step: verify account summary updated
        baseAwait(2) {
            val summaries = jdbcTemplate.findAll(AccountSummaryEntity::class.java)
            assertThat(summaries).hasSize(1)

            val summary = summaries.first()
            assertThat(summary.accountId).isEqualTo(ACCOUNT_ID)
            assertThat(summary.ownerId).isEqualTo(OWNER_ID)
            assertThat(summary.balance).isEqualTo(AMOUNT_100)
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
        assertThat(transaction.type).isEqualTo(TransactionType.DEPOSIT)
        assertThat(transaction.amount).isEqualTo(AMOUNT_50)
        assertThat(transaction.timestamp).isCloseToNow()
        assertThat(transaction.relatedAccountId).isNull()
        assertThat(transaction.isNewEntity).isFalse()
    }

    @Test
    fun `happy path - should not process DepositPerformedEvent twice`() {
        // given: account summary saved
        val eventId1 = idGenerator.generateEventId()
        val eventId2 = idGenerator.generateEventId()

        val entity =
            AccountSummaryEntity(
                accountId = ACCOUNT_ID,
                ownerId = OWNER_ID,
                balance = AMOUNT_50,
                updatedAt = INSTANT,
                sourceEventId = eventId1,
                isNewEntity = true,
            )
        jdbcTemplate.save(entity)

        // given: transaction saved
        val transaction =
            TransactionEntity(
                transactionId = idGenerator.generateTransactionId(),
                accountId = ACCOUNT_ID,
                sourceEventId = eventId2,
                type = TransactionType.DEPOSIT,
                amount = AMOUNT_50,
                timestamp = INSTANT_2,
                relatedAccountId = null,
                isNewEntity = true,
            )

        jdbcTemplate.save(transaction)

        // given
        val event =
            DepositPerformedEvent(
                eventId = eventId2,
                accountId = ACCOUNT_ID,
                amount = AMOUNT_50,
            )

        // step: publish
        eventKafkaTemplate.send(event.toProducerRecord())

        // step: verify account summary has not been changed
        baseAwait {
            val entities = jdbcTemplate.findAll(AccountSummaryEntity::class.java)
            assertThat(entities).hasSize(1)

            val expectedEvent = entity.copy(isNewEntity = false)
            assertThat(entities.first()).isEqualTo(expectedEvent)
        }

        // step: verify transaction has not been added
        val transactions = jdbcTemplate.findAll(TransactionEntity::class.java)
        assertThat(transactions).hasSize(1)

        val expectedTransaction = transaction.copy(isNewEntity = false)
        assertThat(transactions.first()).isEqualTo(expectedTransaction)
    }
}
