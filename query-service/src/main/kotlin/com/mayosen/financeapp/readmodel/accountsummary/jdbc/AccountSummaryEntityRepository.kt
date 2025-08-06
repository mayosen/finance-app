package com.mayosen.financeapp.readmodel.accountsummary.jdbc

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface AccountSummaryEntityRepository : CrudRepository<AccountSummaryEntity, String> {
    fun findAllByOwnerId(ownerId: String): List<AccountSummaryEntity>
}
