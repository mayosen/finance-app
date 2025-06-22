package com.mayosen.financeapp.projection.account.jdbc

import com.mayosen.financeapp.projection.account.AccountSummary
import com.mayosen.financeapp.projection.account.AccountSummaryStore
import org.springframework.stereotype.Component
import kotlin.jvm.optionals.getOrNull

@Component
class JdbcAccountSummaryStore(
    private val accountSummaryEntityRepository: AccountSummaryEntityRepository,
) : AccountSummaryStore {
    override fun findAllByOwnerId(ownerId: String): List<AccountSummary> {
        val entities = accountSummaryEntityRepository.findAllByOwnerId(ownerId)
        return entities
            .map { it.toAccountSummary() }
    }

    override fun findByAccountId(accountId: String): AccountSummary? {
        val entity = accountSummaryEntityRepository.findById(accountId)
        return entity
            .map { it.toAccountSummary() }
            .getOrNull()
    }

    override fun save(summary: AccountSummary) {
        val existsById = accountSummaryEntityRepository.existsById(summary.accountId)
        val entity = summary.toAccountSummaryEntity(!existsById)
        accountSummaryEntityRepository.save(entity)
    }

    override fun saveAll(summaries: List<AccountSummary>) {
        summaries.forEach { save(it) }
    }

    private fun AccountSummaryEntity.toAccountSummary() =
        AccountSummary(
            accountId = accountId,
            balance = balance,
            ownerId = ownerId,
            updatedAt = updatedAt,
        )

    private fun AccountSummary.toAccountSummaryEntity(isNewEntity: Boolean) =
        AccountSummaryEntity(
            accountId = accountId,
            balance = balance,
            ownerId = ownerId,
            updatedAt = updatedAt,
            isNewEntity = isNewEntity,
        )

    override fun deleteByAccountId(accountId: String) {
        if (accountSummaryEntityRepository.existsById(accountId)) {
            accountSummaryEntityRepository.deleteById(accountId)
        }
        // TODO: Else what?
    }
}
