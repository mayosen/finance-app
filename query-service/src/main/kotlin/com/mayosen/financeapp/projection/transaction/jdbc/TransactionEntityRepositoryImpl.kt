package com.mayosen.financeapp.projection.transaction.jdbc

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
class TransactionEntityRepositoryImpl(
    private val jdbcTemplate: NamedParameterJdbcTemplate,
) : TransactionEntityCustomRepository {
    override fun findByFilters(
        accountId: String,
        from: Instant?,
        to: Instant?,
        offset: Int,
        limit: Int,
    ): List<TransactionEntity> {
        val sql =
            buildString {
                append("SELECT * ")
                append("FROM transaction ")
                append("WHERE account_id = :accountId ")
                if (from != null) append("AND timestamp >= :from ")
                if (to != null) append("AND timestamp <= :to ")
                append("ORDER BY timestamp DESC ")
                append("LIMIT :limit OFFSET :offset ")
            }
        val params =
            MapSqlParameterSource()
                .addValue("accountId", accountId)
                .addValue("limit", limit)
                .addValue("offset", offset)

        from?.let { params.addValue("from", it) }
        to?.let { params.addValue("to", it) }

        return jdbcTemplate.query(sql, params, TransactionEntityRowMapper)
    }

    override fun countByFilters(
        accountId: String,
        from: Instant?,
        to: Instant?,
    ): Int {
        val sql =
            buildString {
                append("SELECT COUNT(*) ")
                append("FROM transaction ")
                append("WHERE account_id = :accountId ")
                if (from != null) append("AND timestamp >= :from ")
                if (to != null) append("AND timestamp <= :to ")
            }

        val params =
            MapSqlParameterSource()
                .addValue("accountId", accountId)

        from?.let { params.addValue("from", it) }
        to?.let { params.addValue("to", it) }

        return jdbcTemplate.queryForObject(sql, params, Int::class.java)!!
    }
}
