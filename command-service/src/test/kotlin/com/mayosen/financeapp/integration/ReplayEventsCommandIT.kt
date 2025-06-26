package com.mayosen.financeapp.integration

import com.mayosen.financeapp.event.AccountCreatedEvent
import com.mayosen.financeapp.event.DepositPerformedEvent
import com.mayosen.financeapp.event.ResetProjectionsEvent
import com.mayosen.financeapp.event.jdbc.EventEntity
import com.mayosen.financeapp.test.ACCOUNT_ID
import com.mayosen.financeapp.test.AMOUNT_50
import com.mayosen.financeapp.test.EVENT_ID
import com.mayosen.financeapp.test.EVENT_ID_2
import com.mayosen.financeapp.test.EVENT_ID_3
import com.mayosen.financeapp.test.OWNER_ID
import com.mayosen.financeapp.test.SEQUENCE_NUMBER_2
import com.mayosen.financeapp.test.assertions.assertHasType
import com.mayosen.financeapp.test.assertions.assertHasValueOfType
import com.mayosen.financeapp.test.assertions.isCloseToNow
import com.mayosen.financeapp.test.context.BaseIntegrationTest
import com.mayosen.financeapp.test.generator.generateAdminCommandResponse
import com.mayosen.financeapp.test.generator.generateReplayEventsRequest
import net.javacrumbs.jsonunit.spring.JsonUnitResultMatchers.json
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class ReplayEventsCommandIT : BaseIntegrationTest() {
    @Test
    fun `happy path - should replay events`() {
        // given: account created
        val accountCreatedEvent =
            eventEntityOf(
                AccountCreatedEvent(idGenerator.generateEventId(), ACCOUNT_ID, ownerId = OWNER_ID),
            )
        jdbcTemplate.save(accountCreatedEvent)

        // given: deposit performed
        val depositPerformedEvent =
            eventEntityOf(
                DepositPerformedEvent(idGenerator.generateEventId(), ACCOUNT_ID, amount = AMOUNT_50),
                SEQUENCE_NUMBER_2,
            )
        jdbcTemplate.save(depositPerformedEvent)

        // given
        val requestBody = stringBodyOf(generateReplayEventsRequest())

        // step: request
        val resultActions =
            mockMvc.perform(
                post("/admin-api/v1/replay")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody),
            )

        // step: verify response
        val responseBody = stringBodyOf(generateAdminCommandResponse())
        resultActions
            .andExpect(status().isOk)
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(json().isEqualTo(responseBody))

        // step: verify events not changed
        val events = jdbcTemplate.findAll(EventEntity::class.java)
        assertThat(events).hasSize(2)

        // step: verify reset projections event published
        val record1 = eventsCustomer.pollNotNull()
        assertThat(record1.key()).isEqualTo(EVENT_ID_3)
        record1.headers().assertHasType(ResetProjectionsEvent::class)

        val event1 = record1.assertHasValueOfType(ResetProjectionsEvent::class)
        assertThat(event1.eventId).isEqualTo(EVENT_ID_3)
        assertThat(event1.accountId).isEqualTo(ACCOUNT_ID)
        assertThat(event1.timestamp).isCloseToNow()

        // step: verify account created event published
        val record2 = eventsCustomer.pollNotNull()
        assertThat(record2.key()).isEqualTo(EVENT_ID)
        record2.headers().assertHasType(AccountCreatedEvent::class)

        val event2 = record2.assertHasValueOfType(AccountCreatedEvent::class)
        assertThat(event2.eventId).isEqualTo(EVENT_ID)
        assertThat(event2.accountId).isEqualTo(ACCOUNT_ID)
        assertThat(event2.timestamp).isCloseToNow()
        assertThat(event2.ownerId).isEqualTo(OWNER_ID)

        // step: verify deposit performed event published
        val record3 = eventsCustomer.pollNotNull()
        assertThat(record3.key()).isEqualTo(EVENT_ID_2)
        record3.headers().assertHasType(DepositPerformedEvent::class)

        val event3 = record3.assertHasValueOfType(DepositPerformedEvent::class)
        assertThat(event3.eventId).isEqualTo(EVENT_ID_2)
        assertThat(event3.accountId).isEqualTo(ACCOUNT_ID)
        assertThat(event3.timestamp).isCloseToNow()
        assertThat(event3.amount).isEqualTo(AMOUNT_50)
    }
}
