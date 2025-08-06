package com.mayosen.financeapp.query.api.v1

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.Valid

/**
 *
 * @param transactionId
 * @param type
 * @param amount
 * @param timestamp
 * @param relatedAccountId
 */
data class Transaction(
    @Schema(example = "e1be0f53-007a-4d58-805b-2c5a64fd3ff4", required = true, description = "")
    @get:JsonProperty("transactionId", required = true) val transactionId: String,
    @field:Valid
    @Schema(example = "null", required = true, description = "")
    @get:JsonProperty("type", required = true) val type: TransactionType,
    @field:Valid
    @Schema(example = "50.00", required = true, description = "")
    @get:JsonProperty("amount", required = true) val amount: java.math.BigDecimal,
    @Schema(example = "2025-05-15T12:34:56Z", required = true, description = "")
    @get:JsonProperty("timestamp", required = true) val timestamp: java.time.Instant,
    @Schema(example = "e1be0f53-007a-4d58-805b-2c5a64fd3ff4", description = "")
    @get:JsonProperty("relatedAccountId") val relatedAccountId: String? = null,
)
