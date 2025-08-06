package com.mayosen.financeapp.projection.transaction.jdbc

import org.springframework.data.jdbc.repository.query.Modifying
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface TransactionEntityRepository :
    CrudRepository<TransactionEntity, String>,
    TransactionEntityCustomRepository {
    fun existsBySourceEventId(sourceEventId: String): Boolean

    @Query("DELETE FROM transaction WHERE account_id = :accountId")
    @Modifying
    fun deleteAllByAccountId(accountId: String)
}
