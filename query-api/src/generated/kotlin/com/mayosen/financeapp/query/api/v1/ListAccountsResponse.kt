package com.mayosen.financeapp.query.api.v1

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.Valid

/**
 *
 * @param accounts
 */
data class ListAccountsResponse(
    @field:Valid
    @Schema(example = "null", required = true, description = "")
    @get:JsonProperty("accounts", required = true) val accounts: List<Account>,
)
