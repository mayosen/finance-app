package com.mayosen.financeapp.integration.query

import com.mayosen.financeapp.projection.account.jdbc.AccountSummaryEntity
import com.mayosen.financeapp.test.ACCOUNT_ID
import com.mayosen.financeapp.test.AMOUNT_50
import com.mayosen.financeapp.test.EVENT_ID
import com.mayosen.financeapp.test.INSTANT
import com.mayosen.financeapp.test.OWNER_ID
import com.mayosen.financeapp.test.context.BaseIntegrationTest
import com.mayosen.financeapp.test.generator.generateGetAccountSummaryResponse
import net.javacrumbs.jsonunit.spring.JsonUnitResultMatchers.json
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class GetAccountSummaryIT : BaseIntegrationTest() {
    @Test
    fun `happy path - should return account summary`() {
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

        // step: request
        val resultActions =
            mockMvc.perform(
                get("/query-api/v1/accounts/summary")
                    .queryParam("accountId", ACCOUNT_ID),
            )

        // step: verify response
        val responseBody = stringBodyOf(generateGetAccountSummaryResponse())
        resultActions
            .andExpect(status().isOk)
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(json().isEqualTo(responseBody))
    }
}
