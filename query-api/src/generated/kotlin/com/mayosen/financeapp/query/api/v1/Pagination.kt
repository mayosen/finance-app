package com.mayosen.financeapp.query.api.v1

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema

/**
 *
 * @param hasMore
 * @param total
 */
data class Pagination(
    @Schema(example = "false", required = true, description = "")
    @get:JsonProperty("hasMore", required = true) val hasMore: Boolean,
    @Schema(example = "15", required = true, description = "")
    @get:JsonProperty("total", required = true) val total: Int,
)
