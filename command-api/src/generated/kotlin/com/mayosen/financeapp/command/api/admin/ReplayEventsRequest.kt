package com.mayosen.financeapp.command.api.admin

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema

/**
 *
 * @param accountId
 */
data class ReplayEventsRequest(
    @Schema(example = "acc123", required = true, description = "")
    @get:JsonProperty("accountId", required = true) val accountId: String,
)
