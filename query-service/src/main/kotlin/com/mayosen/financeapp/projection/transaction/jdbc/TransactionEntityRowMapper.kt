package com.mayosen.financeapp.projection.transaction.jdbc

import com.mayosen.financeapp.util.serialization.TimestampToInstantConverter
import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet
import java.time.Instant

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
            timestamp = rs.getTimestamp(),
            relatedAccountId = rs.getString("related_account_id"),
        )

    private fun ResultSet.getTransactionType(): TransactionEntity.TransactionType {
        val value = getString("type")
        return TransactionEntity.TransactionType.valueOf(value)
    }

    private fun ResultSet.getTimestamp(): Instant {
        val value = getTimestamp("timestamp")
        return TimestampToInstantConverter.convert(value)
    }
}
