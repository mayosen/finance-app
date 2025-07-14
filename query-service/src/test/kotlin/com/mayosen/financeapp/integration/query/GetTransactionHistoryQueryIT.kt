package com.mayosen.financeapp.integration.query

import com.mayosen.financeapp.projection.transaction.jdbc.TransactionEntity
import com.mayosen.financeapp.query.api.v1.TransactionType
import com.mayosen.financeapp.test.ACCOUNT_ID
import com.mayosen.financeapp.test.AMOUNT_100
import com.mayosen.financeapp.test.AMOUNT_50
import com.mayosen.financeapp.test.EVENT_ID
import com.mayosen.financeapp.test.EVENT_ID_2
import com.mayosen.financeapp.test.INSTANT
import com.mayosen.financeapp.test.INSTANT_2
import com.mayosen.financeapp.test.TRANSACTION_ID
import com.mayosen.financeapp.test.TRANSACTION_ID_2
import com.mayosen.financeapp.test.context.BaseIntegrationTest
import com.mayosen.financeapp.test.generator.generateGetTransactionHistoryResponse
import com.mayosen.financeapp.test.generator.generatePagination
import com.mayosen.financeapp.test.generator.generateTransaction
import net.javacrumbs.jsonunit.spring.JsonUnitResultMatchers.json
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.temporal.ChronoUnit

class GetTransactionHistoryQueryIT : BaseIntegrationTest() {
    @Test
    fun `happy path - should return transaction history`() {
        // given: transactions saved
        val transaction1 =
            TransactionEntity(
                transactionId = TRANSACTION_ID,
                accountId = ACCOUNT_ID,
                sourceEventId = EVENT_ID,
                type = TransactionEntity.TransactionType.DEPOSIT,
                amount = AMOUNT_50,
                timestamp = INSTANT,
                relatedAccountId = null,
                isNewEntity = true,
            )
        val transaction2 =
            TransactionEntity(
                transactionId = TRANSACTION_ID_2,
                accountId = ACCOUNT_ID,
                sourceEventId = EVENT_ID_2,
                type = TransactionEntity.TransactionType.DEPOSIT,
                amount = AMOUNT_100,
                timestamp = INSTANT_2,
                relatedAccountId = null,
                isNewEntity = true,
            )
        jdbcTemplate.saveAll(listOf(transaction1, transaction2))

        // step: request
        val resultActions =
            mockMvc.perform(
                get("/query-api/v1/transactions")
                    .queryParam("accountId", ACCOUNT_ID)
                    .queryParam("timeFrom", INSTANT.minus(1L, ChronoUnit.DAYS).toString())
                    .queryParam("timeTo", INSTANT.plus(1L, ChronoUnit.DAYS).toString()),
            )

        // step: verify response
        val responseBody = stringBodyOf(generateGetTransactionHistoryResponse())
        resultActions
            .andExpect(status().isOk)
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(json().isEqualTo(responseBody))
    }

    @Test
    fun `happy path - should return a page`() {
        // given: transactions saved
        val transaction1 =
            TransactionEntity(
                transactionId = TRANSACTION_ID,
                accountId = ACCOUNT_ID,
                sourceEventId = EVENT_ID,
                type = TransactionEntity.TransactionType.DEPOSIT,
                amount = AMOUNT_50,
                timestamp = INSTANT,
                relatedAccountId = null,
                isNewEntity = true,
            )
        val transaction2 =
            TransactionEntity(
                transactionId = TRANSACTION_ID_2,
                accountId = ACCOUNT_ID,
                sourceEventId = EVENT_ID_2,
                type = TransactionEntity.TransactionType.DEPOSIT,
                amount = AMOUNT_100,
                timestamp = INSTANT_2,
                relatedAccountId = null,
                isNewEntity = true,
            )
        jdbcTemplate.saveAll(listOf(transaction1, transaction2))

        // step: request
        val resultActions =
            mockMvc.perform(
                get("/query-api/v1/transactions")
                    .queryParam("accountId", ACCOUNT_ID)
                    .queryParam("pageLimit", 1.toString()),
            )

        // step: verify response
        val responseBody =
            stringBodyOf(
                generateGetTransactionHistoryResponse(
                    pagination = generatePagination(hasMore = true, total = 2),
                ),
            )
        resultActions
            .andExpect(status().isOk)
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(json().isEqualTo(responseBody))
    }

    @Test
    fun `happy path - should return a page with offset`() {
        // given: transactions saved
        val transaction1 =
            TransactionEntity(
                transactionId = TRANSACTION_ID,
                accountId = ACCOUNT_ID,
                sourceEventId = EVENT_ID,
                type = TransactionEntity.TransactionType.DEPOSIT,
                amount = AMOUNT_50,
                timestamp = INSTANT,
                relatedAccountId = null,
                isNewEntity = true,
            )
        val transaction2 =
            TransactionEntity(
                transactionId = TRANSACTION_ID_2,
                accountId = ACCOUNT_ID,
                sourceEventId = EVENT_ID_2,
                type = TransactionEntity.TransactionType.DEPOSIT,
                amount = AMOUNT_100,
                timestamp = INSTANT_2,
                relatedAccountId = null,
                isNewEntity = true,
            )
        jdbcTemplate.saveAll(listOf(transaction1, transaction2))

        // step: request
        val resultActions =
            mockMvc.perform(
                get("/query-api/v1/transactions")
                    .queryParam("accountId", ACCOUNT_ID)
                    .queryParam("pageOffset", 1.toString())
                    .queryParam("pageLimit", 1.toString()),
            )

        // step: verify response
        val responseBody =
            stringBodyOf(
                generateGetTransactionHistoryResponse(
                    transactions =
                        listOf(
                            generateTransaction(
                                transactionId = TRANSACTION_ID_2,
                                transactionType = TransactionType.DEPOSIT,
                                amount = AMOUNT_100,
                                timestamp = INSTANT_2,
                            ),
                        ),
                    pagination = generatePagination(total = 2),
                ),
            )
        resultActions
            .andExpect(status().isOk)
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(json().isEqualTo(responseBody))
    }
}
