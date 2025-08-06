package com.mayosen.financeapp.readmodel.transactionhistory.jdbc

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface TransactionViewEntityRepository :
    CrudRepository<TransactionViewEntity, String>,
    TransactionViewEntityCustomRepository {
    fun deleteAllByAccountId(accountId: String): Int
}
