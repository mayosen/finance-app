package com.mayosen.financeapp.command.api.v1

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.Valid

/**
 *
 * @param fromAccountId
 * @param toAccountId
 * @param amount
 */
data class TransferRequest(
    @Schema(example = "e1be0f53-007a-4d58-805b-2c5a64fd3ff4", required = true, description = "")
    @get:JsonProperty("fromAccountId", required = true) val fromAccountId: String,
    @Schema(example = "e1be0f53-007a-4d58-805b-2c5a64fd3ff4", required = true, description = "")
    @get:JsonProperty("toAccountId", required = true) val toAccountId: String,
    @field:Valid
    @Schema(example = "50.00", required = true, description = "")
    @get:JsonProperty("amount", required = true) val amount: java.math.BigDecimal,
)
