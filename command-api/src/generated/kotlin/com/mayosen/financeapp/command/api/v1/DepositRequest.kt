package com.mayosen.financeapp.command.api.v1

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.Valid

/**
 *
 * @param accountId
 * @param amount
 */
data class DepositRequest(
    @Schema(example = "e1be0f53-007a-4d58-805b-2c5a64fd3ff4", required = true, description = "")
    @get:JsonProperty("accountId", required = true) val accountId: String,
    @field:Valid
    @Schema(example = "50.00", required = true, description = "")
    @get:JsonProperty("amount", required = true) val amount: java.math.BigDecimal,
)
