package com.mayosen.financeapp.integration.snapshot

import com.mayosen.financeapp.event.AccountCreatedEvent
import com.mayosen.financeapp.event.DepositPerformedEvent
import com.mayosen.financeapp.event.jdbc.EventEntity
import com.mayosen.financeapp.snapshot.jdbc.AccountSnapshotEntity
import com.mayosen.financeapp.test.ACCOUNT_ID
import com.mayosen.financeapp.test.AMOUNT_0
import com.mayosen.financeapp.test.AMOUNT_50
import com.mayosen.financeapp.test.EVENT_ID_3
import com.mayosen.financeapp.test.OWNER_ID
import com.mayosen.financeapp.test.SEQUENCE_NUMBER_2
import com.mayosen.financeapp.test.SEQUENCE_NUMBER_3
import com.mayosen.financeapp.test.assertions.isCloseToNow
import com.mayosen.financeapp.test.context.BaseIntegrationTest
import com.mayosen.financeapp.test.generator.generateCommandResponse
import com.mayosen.financeapp.test.generator.generateWithdrawRequest
import net.javacrumbs.jsonunit.spring.JsonUnitResultMatchers.json
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class CreateSnapshotIT : BaseIntegrationTest() {
    @Test
    fun `happy path - should create snapshot`() {
        // given: account created
        val accountCreatedEvent = AccountCreatedEvent(idGenerator.generateEventId(), ACCOUNT_ID, ownerId = OWNER_ID)
        jdbcTemplate.save(eventEntityOf(accountCreatedEvent))

        // given: deposit performed
        val depositPerformedEvent = DepositPerformedEvent(idGenerator.generateEventId(), ACCOUNT_ID, amount = AMOUNT_50)
        jdbcTemplate.save(eventEntityOf(depositPerformedEvent, SEQUENCE_NUMBER_2))

        // given
        val requestBody = stringBodyOf(generateWithdrawRequest())

        // step: request withdraw
        val resultActions =
            mockMvc.perform(
                post("/command-api/v1/withdraw")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody),
            )

        // step: verify response
        val responseBody = stringBodyOf(generateCommandResponse())
        resultActions
            .andExpect(status().isOk)
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(json().isEqualTo(responseBody))

        // step: verify event saved
        val events = jdbcTemplate.findAll(EventEntity::class.java)
        assertThat(events).hasSize(3)

        // step: verify snapshot saved
        val snapshots = jdbcTemplate.findAll(AccountSnapshotEntity::class.java)
        assertThat(snapshots).hasSize(1)

        val snapshot = snapshots.first()
        assertThat(snapshot.accountId).isEqualTo(ACCOUNT_ID)
        assertThat(snapshot.balance).isEqualTo(AMOUNT_0)
        assertThat(snapshot.lastSequenceNumber).isEqualTo(SEQUENCE_NUMBER_3)
        assertThat(snapshot.timestamp).isCloseToNow()

        val record = eventsCustomer.pollNotNull()
        assertThat(record.key()).isEqualTo(EVENT_ID_3)
    }
}
