package com.mayosen.financeapp.projection.transaction.jdbc

import com.mayosen.financeapp.projection.transaction.Pagination
import com.mayosen.financeapp.projection.transaction.TimePeriod
import com.mayosen.financeapp.projection.transaction.Transaction
import com.mayosen.financeapp.projection.transaction.TransactionHistory
import com.mayosen.financeapp.projection.transaction.TransactionStore
import com.mayosen.financeapp.projection.transaction.jdbc.mapper.DtoToEntityMapper.toEntity
import com.mayosen.financeapp.projection.transaction.jdbc.mapper.EntityToDtoMapper.toTransaction
import org.apache.logging.log4j.kotlin.Logging
import org.springframework.stereotype.Component

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

        val transactions = entities.map { it.toTransaction() }
        val hasMore = pagination.offset + transactions.size < total
        val paginationDto =
            TransactionHistory.Pagination(
                hasMore = hasMore,
                total = total,
            )

        return TransactionHistory(transactions = transactions, pagination = paginationDto)
    }

    override fun save(transaction: Transaction) {
        val entity = transaction.toEntity()
        transactionEntityRepository.save(entity)
    }

    override fun saveAll(transactions: List<Transaction>) {
        val entities = transactions.map { it.toEntity() }
        transactionEntityRepository.saveAll(entities)
    }

    override fun deleteAllByAccountId(accountId: String) {
        transactionEntityRepository.deleteAllByAccountId(accountId)
    }

    private companion object : Logging
}
