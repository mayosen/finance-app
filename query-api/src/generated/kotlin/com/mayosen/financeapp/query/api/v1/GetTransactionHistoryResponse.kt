package com.mayosen.financeapp.query.api.v1

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.Valid

/**
 *
 * @param transactions
 * @param pagination
 */
data class GetTransactionHistoryResponse(
    @field:Valid
    @Schema(example = "null", required = true, description = "")
    @get:JsonProperty("transactions", required = true) val transactions: List<Transaction>,
    @field:Valid
    @Schema(example = "null", required = true, description = "")
    @get:JsonProperty("pagination", required = true) val pagination: Pagination,
)
