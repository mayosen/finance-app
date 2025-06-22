package com.mayosen.financeapp.projection.transaction.jdbc

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface TransactionEntityRepository :
    CrudRepository<TransactionEntity, String>,
    TransactionEntityCustomRepository {
    fun deleteAllByAccountId(accountId: String): Int
}
