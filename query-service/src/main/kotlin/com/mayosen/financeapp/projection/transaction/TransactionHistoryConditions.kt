package com.mayosen.financeapp.projection.transaction

import java.time.Instant

class TimePeriod(
    val from: Instant? = null,
    val to: Instant? = null,
)

class Pagination(
    val offset: Int,
    val limit: Int,
)
