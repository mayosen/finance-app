package com.mayosen.financeapp.integration.event

import com.mayosen.financeapp.event.AccountDeletedEvent
import com.mayosen.financeapp.projection.account.jdbc.AccountSummaryEntity
import com.mayosen.financeapp.projection.transaction.jdbc.TransactionEntity
import com.mayosen.financeapp.projection.transaction.jdbc.TransactionEntity.TransactionType
import com.mayosen.financeapp.test.ACCOUNT_ID
import com.mayosen.financeapp.test.ACCOUNT_ID_2
import com.mayosen.financeapp.test.AMOUNT_0
import com.mayosen.financeapp.test.AMOUNT_50
import com.mayosen.financeapp.test.EVENT_ID
import com.mayosen.financeapp.test.EVENT_ID_2
import com.mayosen.financeapp.test.INSTANT
import com.mayosen.financeapp.test.OWNER_ID
import com.mayosen.financeapp.test.context.BaseIntegrationTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ProjectAccountDeletedEventIT : BaseIntegrationTest() {
    @Test
    fun `happy path - should project AccountDeletedEvent`() {
        // given: account summary saved
        val summaryEntity =
            AccountSummaryEntity(
                accountId = ACCOUNT_ID,
                ownerId = OWNER_ID,
                balance = AMOUNT_0,
                updatedAt = INSTANT,
                sourceEventId = EVENT_ID_2,
                isNewEntity = true,
            )
        jdbcTemplate.save(summaryEntity)

        // given: transactions saved
        val transferIn =
            TransactionEntity(
                transactionId = idGenerator.generateTransactionId(),
                accountId = ACCOUNT_ID,
                sourceEventId = EVENT_ID,
                type = TransactionType.TRANSFER_IN,
                amount = AMOUNT_50,
                timestamp = INSTANT,
                relatedAccountId = ACCOUNT_ID_2,
                isNewEntity = true,
            )
        val transferOut =
            TransactionEntity(
                transactionId = idGenerator.generateTransactionId(),
                accountId = ACCOUNT_ID,
                sourceEventId = EVENT_ID_2,
                type = TransactionType.TRANSFER_OUT,
                amount = AMOUNT_50,
                timestamp = INSTANT,
                relatedAccountId = ACCOUNT_ID_2,
                isNewEntity = true,
            )
        jdbcTemplate.saveAll(listOf(transferIn, transferOut))

        // given
        val event =
            AccountDeletedEvent(
                eventId = idGenerator.generateEventId(),
                accountId = ACCOUNT_ID,
            )

        // step: publish
        eventKafkaTemplate.send(event.toProducerRecord())

        // step: verify account summary deleted
        baseAwait(2) {
            val summaries = jdbcTemplate.findAll(AccountSummaryEntity::class.java)
            assertThat(summaries).isEmpty()
        }

        // step: verify transactions deleted
        val transactions = jdbcTemplate.findAll(TransactionEntity::class.java)
        assertThat(transactions).isEmpty()
    }
}
