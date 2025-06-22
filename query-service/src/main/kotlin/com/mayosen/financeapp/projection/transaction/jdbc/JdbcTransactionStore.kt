package com.mayosen.financeapp.projection.transaction.jdbc

import com.mayosen.financeapp.projection.transaction.Pagination
import com.mayosen.financeapp.projection.transaction.TimePeriod
import com.mayosen.financeapp.projection.transaction.Transaction
import com.mayosen.financeapp.projection.transaction.TransactionHistory
import com.mayosen.financeapp.projection.transaction.TransactionStore
import com.mayosen.financeapp.projection.transaction.TransactionType
import org.springframework.stereotype.Component
import org.springframework.util.Assert

@Component
class JdbcTransactionStore(
    private val transactionEntityRepository: TransactionEntityRepository,
) : TransactionStore {
    override fun findByAccountId(
        accountId: String,
        timePeriod: TimePeriod?,
        pagination: Pagination,
    ): TransactionHistory {
        val from = timePeriod?.from
        val to = timePeriod?.to

        val total = transactionEntityRepository.countByFilters(accountId, from, to)
        val entities =
            transactionEntityRepository
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

    private fun TransactionEntity.toTransaction() =
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
        transactionEntityRepository.save(entity)
    }

    override fun saveAll(transactions: List<Transaction>) {
        val entities = transactions.map { it.toViewEntity() }
        transactionEntityRepository.saveAll(entities)
    }

    private fun Transaction.toViewEntity(): TransactionEntity =
        TransactionEntity(
            transactionId = sourceEventId,
            accountId = accountId,
            sourceEventId = sourceEventId,
            // TODO: Map explicitly
            type = com.mayosen.financeapp.projection.transaction.jdbc.TransactionEntity.TransactionType.valueOf(type.name),
            amount = amount,
            timestamp = timestamp,
            relatedAccountId = relatedAccountId,
            isNewEntity = true,
        )

    override fun deleteAllByAccountId(accountId: String) {
        val transactionsDeleted = transactionEntityRepository.deleteAllByAccountId(accountId)
        // TODO: log number
    }
}
