package com.mayosen.financeapp.readmodel.transactionhistory

import java.time.Instant

class TimePeriod(
    val from: Instant? = null,
    val to: Instant? = null,
)

class Pagination(
    val offset: Int,
    val limit: Int,
)
