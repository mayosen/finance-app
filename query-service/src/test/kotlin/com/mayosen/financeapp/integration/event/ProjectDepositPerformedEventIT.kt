package com.mayosen.financeapp.integration.event

import com.mayosen.financeapp.event.DepositPerformedEvent
import com.mayosen.financeapp.projection.account.jdbc.AccountSummaryEntity
import com.mayosen.financeapp.test.ACCOUNT_ID
import com.mayosen.financeapp.test.AMOUNT_0
import com.mayosen.financeapp.test.AMOUNT_100
import com.mayosen.financeapp.test.AMOUNT_50
import com.mayosen.financeapp.test.EVENT_ID
import com.mayosen.financeapp.test.INSTANT
import com.mayosen.financeapp.test.OWNER_ID
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
                sourceEventId = EVENT_ID,
                isNewEntity = true,
            )
        jdbcTemplate.save(entity)

        // given
        val event =
            DepositPerformedEvent(
                eventId = EVENT_ID,
                accountId = ACCOUNT_ID,
                amount = AMOUNT_50,
            )

        // step: publish
        eventKafkaTemplate.send(event.toProducerRecord())

        // step: verify AccountSummary saved
        baseAwait {
            val summaries = jdbcTemplate.findAll(AccountSummaryEntity::class.java)
            assertThat(summaries).hasSize(1)

            val summary = summaries.first()
            assertThat(summary.accountId).isEqualTo(ACCOUNT_ID)
            assertThat(summary.ownerId).isEqualTo(OWNER_ID)
            assertThat(summary.balance).isEqualTo(AMOUNT_100)
            assertThat(summary.updatedAt).isCloseToNow()
            assertThat(summary.isNewEntity).isFalse()
        }
    }
}
