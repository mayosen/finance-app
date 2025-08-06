package com.mayosen.financeapp.command.api.v1

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema

/**
 *
 * @param id
 */
data class CommandResponse(
    @Schema(example = "e1be0f53-007a-4d58-805b-2c5a64fd3ff4", required = true, description = "")
    @get:JsonProperty("id", required = true) val id: String,
)
