package com.mayosen.financeapp.readmodel.transactionhistory.jdbc

import com.mayosen.financeapp.readmodel.transactionhistory.Pagination
import com.mayosen.financeapp.readmodel.transactionhistory.TimePeriod
import com.mayosen.financeapp.readmodel.transactionhistory.Transaction
import com.mayosen.financeapp.readmodel.transactionhistory.TransactionHistory
import com.mayosen.financeapp.readmodel.transactionhistory.TransactionHistoryStore
import com.mayosen.financeapp.readmodel.transactionhistory.TransactionType
import org.springframework.stereotype.Component
import org.springframework.util.Assert

@Component
class JdbcTransactionHistoryStore(
    private val transactionViewEntityRepository: TransactionViewEntityRepository,
) : TransactionHistoryStore {
    override fun findByAccountId(
        accountId: String,
        timePeriod: TimePeriod?,
        pagination: Pagination,
    ): TransactionHistory {
        val from = timePeriod?.from
        val to = timePeriod?.to

        val total = transactionViewEntityRepository.countByFilters(accountId, from, to)
        val entities =
            transactionViewEntityRepository
                .findByFilters(
                    accountId = accountId,
                    from = from,
                    to = to,
                    offset = pagination.offset,
                    limit = pagination.limit,
                )

        Assert.isTrue(entities.size == total) {
            "Number of entities was expected to be equal to $total, but was ${entities.size}"
        }

        val transactions = entities.map { it.toTransaction() }
        val hasMore = pagination.offset + transactions.size < total

        return TransactionHistory(
            transactions = transactions,
            pagination =
                TransactionHistory.Pagination(
                    hasMore = hasMore,
                    total = total,
                ),
        )
    }

    private fun TransactionViewEntity.toTransaction() =
        Transaction(
            accountId = accountId,
            transactionId = transactionId,
            sourceEventId = transactionId,
            // TODO: Map explicitly
            type = TransactionType.valueOf(type.name),
            amount = amount,
            timestamp = timestamp,
            relatedAccountId = relatedAccountId,
        )

    override fun save(transaction: Transaction) {
        val entity = transaction.toViewEntity()
        transactionViewEntityRepository.save(entity)
    }

    override fun saveAll(transactions: List<Transaction>) {
        val entities = transactions.map { it.toViewEntity() }
        transactionViewEntityRepository.saveAll(entities)
    }

    private fun Transaction.toViewEntity(): TransactionViewEntity =
        TransactionViewEntity(
            transactionId = sourceEventId,
            accountId = accountId,
            sourceEventId = sourceEventId,
            // TODO: Map explicitly
            type = TransactionViewEntity.TransactionType.valueOf(type.name),
            amount = amount,
            timestamp = timestamp,
            relatedAccountId = relatedAccountId,
            isNewEntity = true,
        )

    override fun deleteAllByAccountId(accountId: String) {
        val transactionsDeleted = transactionViewEntityRepository.deleteAllByAccountId(accountId)
        // TODO: log number
    }
}
