package com.mayosen.financeapp.integration.event

import com.mayosen.financeapp.event.AccountCreatedEvent
import com.mayosen.financeapp.projection.account.jdbc.AccountSummaryEntity
import com.mayosen.financeapp.test.ACCOUNT_ID
import com.mayosen.financeapp.test.AMOUNT_0
import com.mayosen.financeapp.test.EVENT_ID
import com.mayosen.financeapp.test.OWNER_ID
import com.mayosen.financeapp.test.assertions.isCloseToNow
import com.mayosen.financeapp.test.context.BaseIntegrationTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ProjectAccountCreatedEventIT : BaseIntegrationTest() {
    @Test
    fun `happy path - should project AccountCreatedEvent`() {
        // given
        val event =
            AccountCreatedEvent(
                eventId = idGenerator.generateEventId(),
                accountId = idGenerator.generateAccountId(),
                ownerId = OWNER_ID,
            )

        // step: publish
        eventKafkaTemplate.send(event.toProducerRecord())

        // step: verify AccountSummary saved
        baseAwait {
            val entities = jdbcTemplate.findAll(AccountSummaryEntity::class.java)
            assertThat(entities).hasSize(1)

            val entity = entities.first()
            assertThat(entity.accountId).isEqualTo(ACCOUNT_ID)
            assertThat(entity.ownerId).isEqualTo(OWNER_ID)
            assertThat(entity.balance).isEqualTo(AMOUNT_0)
            assertThat(entity.updatedAt).isCloseToNow()
            assertThat(entity.sourceEventId).isEqualTo(EVENT_ID)
            assertThat(entity.isNewEntity).isFalse()
        }
    }
}
