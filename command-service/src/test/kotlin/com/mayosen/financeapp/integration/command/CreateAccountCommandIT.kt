package com.mayosen.financeapp.integration.command

import com.mayosen.financeapp.event.AccountCreatedEvent
import com.mayosen.financeapp.event.jdbc.EventEntity
import com.mayosen.financeapp.event.serialization.typeName
import com.mayosen.financeapp.test.ACCOUNT_ID
import com.mayosen.financeapp.test.EVENT_ID
import com.mayosen.financeapp.test.OWNER_ID
import com.mayosen.financeapp.test.SEQUENCE_NUMBER
import com.mayosen.financeapp.test.assertions.assertHasType
import com.mayosen.financeapp.test.assertions.assertHasValueOfType
import com.mayosen.financeapp.test.assertions.isCloseToNow
import com.mayosen.financeapp.test.context.BaseIntegrationTest
import com.mayosen.financeapp.test.generator.generateCommandResponse
import com.mayosen.financeapp.test.generator.generateCreateAccountRequest
import net.javacrumbs.jsonunit.spring.JsonUnitResultMatchers.json
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class CreateAccountCommandIT : BaseIntegrationTest() {
    @Test
    fun `happy path - should create account`() {
        // given
        val requestBody = stringBodyOf(generateCreateAccountRequest())

        // step: request
        val resultActions =
            mockMvc.perform(
                post("/command-api/v1/accounts")
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
        assertThat(events).hasSize(1)

        val entity = events.first()
        assertThat(entity.eventId).isEqualTo(EVENT_ID)
        assertThat(entity.sequenceNumber).isEqualTo(SEQUENCE_NUMBER)
        assertThat(entity.accountId).isEqualTo(ACCOUNT_ID)
        assertThat(entity.eventType).isEqualTo(AccountCreatedEvent::class.typeName())
        assertThat(entity.eventFields).isEqualTo(eventFieldsOf(OWNER_ID_FIELD to OWNER_ID))
        assertThat(entity.timestamp).isCloseToNow()
        assertThat(entity.isNewEntity).isFalse()

        // step: verify event published
        val record = eventsCustomer.pollNotNull()
        assertThat(record.key()).isEqualTo(EVENT_ID)
        record.headers().assertHasType(AccountCreatedEvent::class)

        val event = record.assertHasValueOfType(AccountCreatedEvent::class)
        assertThat(event.eventId).isEqualTo(EVENT_ID)
        assertThat(event.accountId).isEqualTo(ACCOUNT_ID)
        assertThat(event.timestamp).isCloseToNow()
        assertThat(event.ownerId).isEqualTo(OWNER_ID)
    }
}
