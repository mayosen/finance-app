package com.mayosen.financeapp.query.api.v1

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.Valid

/**
 *
 * @param accountId
 * @param balance
 * @param updatedAt
 */
data class Account(
    @Schema(example = "e1be0f53-007a-4d58-805b-2c5a64fd3ff4", required = true, description = "")
    @get:JsonProperty("accountId", required = true) val accountId: String,
    @field:Valid
    @Schema(example = "50.00", required = true, description = "")
    @get:JsonProperty("balance", required = true) val balance: java.math.BigDecimal,
    @Schema(example = "2025-05-15T12:34:56Z", required = true, description = "")
    @get:JsonProperty("updatedAt", required = true) val updatedAt: java.time.Instant,
)
