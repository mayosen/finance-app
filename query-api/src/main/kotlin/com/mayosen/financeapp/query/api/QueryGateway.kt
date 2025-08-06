package com.mayosen.financeapp.query.api

import com.mayosen.financeapp.query.Query

interface QueryGateway {
    fun <T : Query, R : Any> query(query: T): R
}
