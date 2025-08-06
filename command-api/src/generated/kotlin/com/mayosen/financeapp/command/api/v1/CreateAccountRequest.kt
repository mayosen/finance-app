package com.mayosen.financeapp.command.api.v1

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema

/**
 *
 * @param ownerId
 */
data class CreateAccountRequest(
    @Schema(example = "user123", required = true, description = "")
    @get:JsonProperty("ownerId", required = true) val ownerId: String,
)
