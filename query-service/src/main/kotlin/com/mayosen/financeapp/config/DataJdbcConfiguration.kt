package com.mayosen.financeapp.config

import com.mayosen.financeapp.projection.transaction.jdbc.TransactionEntity
import com.mayosen.financeapp.projection.transaction.jdbc.TransactionEntityRowMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.jdbc.repository.QueryMappingConfiguration
import org.springframework.data.jdbc.repository.config.DefaultQueryMappingConfiguration

@Configuration
class DataJdbcConfiguration {
    @Bean
    fun rowMappers(): QueryMappingConfiguration =
        DefaultQueryMappingConfiguration()
            .registerRowMapper(TransactionEntity::class.java, TransactionEntityRowMapper)
}
