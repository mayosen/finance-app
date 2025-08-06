package com.mayosen.financeapp.readmodel.transactionhistory.jdbc

import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet

object TransactionViewEntityRowMapper : RowMapper<TransactionViewEntity> {
    override fun mapRow(
        rs: ResultSet,
        rowNum: Int,
    ): TransactionViewEntity =
        TransactionViewEntity(
            transactionId = rs.getString("transaction_id"),
            accountId = rs.getString("account_id"),
            sourceEventId = rs.getString("source_event_id"),
            // TODO: Map explicitly
            type = TransactionViewEntity.TransactionType.valueOf(rs.getString("type")),
            amount = rs.getBigDecimal("amount"),
            timestamp = rs.getTimestamp("timestamp").toInstant(),
            relatedAccountId = rs.getString("related_account_id"),
        )
}
