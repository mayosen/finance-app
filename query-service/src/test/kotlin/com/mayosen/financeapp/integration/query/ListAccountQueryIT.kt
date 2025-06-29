package com.mayosen.financeapp.integration.query

import com.mayosen.financeapp.projection.account.jdbc.AccountSummaryEntity
import com.mayosen.financeapp.test.ACCOUNT_ID
import com.mayosen.financeapp.test.ACCOUNT_ID_2
import com.mayosen.financeapp.test.AMOUNT_0
import com.mayosen.financeapp.test.AMOUNT_50
import com.mayosen.financeapp.test.EVENT_ID
import com.mayosen.financeapp.test.EVENT_ID_2
import com.mayosen.financeapp.test.INSTANT
import com.mayosen.financeapp.test.INSTANT_2
import com.mayosen.financeapp.test.OWNER_ID
import com.mayosen.financeapp.test.context.BaseIntegrationTest
import com.mayosen.financeapp.test.generator.generateAccount
import com.mayosen.financeapp.test.generator.generateListAccountsResponse
import net.javacrumbs.jsonunit.spring.JsonUnitResultMatchers.json
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class ListAccountQueryIT : BaseIntegrationTest() {
    @Test
    fun `happy path - should return account summary`() {
        // given: account summaries saved
        val summary1 =
            AccountSummaryEntity(
                accountId = ACCOUNT_ID,
                ownerId = OWNER_ID,
                balance = AMOUNT_50,
                updatedAt = INSTANT,
                sourceEventId = EVENT_ID,
                isNewEntity = true,
            )
        val summary2 =
            AccountSummaryEntity(
                accountId = ACCOUNT_ID_2,
                ownerId = OWNER_ID,
                balance = AMOUNT_0,
                updatedAt = INSTANT_2,
                sourceEventId = EVENT_ID_2,
                isNewEntity = true,
            )
        jdbcTemplate.saveAll(listOf(summary1, summary2))

        // step: request
        val resultActions =
            mockMvc.perform(
                get("/query-api/v1/accounts")
                    .queryParam("ownerId", OWNER_ID),
            )

        // step: verify response
        val accounts =
            listOf(
                generateAccount(),
                generateAccount(accountId = ACCOUNT_ID_2, balance = AMOUNT_0, updatedAt = INSTANT_2),
            )
        val responseBody = stringBodyOf(generateListAccountsResponse(accounts))
        resultActions
            .andExpect(status().isOk)
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(json().isEqualTo(responseBody))
    }
}
