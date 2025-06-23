package com.mayosen.financeapp.projection.transaction.jdbc

import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet

object TransactionEntityRowMapper : RowMapper<TransactionEntity> {
    override fun mapRow(
        rs: ResultSet,
        rowNum: Int,
    ): TransactionEntity =
        TransactionEntity(
            transactionId = rs.getString("transaction_id"),
            accountId = rs.getString("account_id"),
            sourceEventId = rs.getString("source_event_id"),
            type = rs.getTransactionType(),
            amount = rs.getBigDecimal("amount"),
            timestamp = rs.getTimestamp("timestamp").toInstant(),
            relatedAccountId = rs.getString("related_account_id"),
        )

    private fun ResultSet.getTransactionType(): TransactionEntity.TransactionType {
        val value = getString("type")
        return TransactionEntity.TransactionType.valueOf(value)
    }
}
