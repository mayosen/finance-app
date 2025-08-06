package com.mayosen.financeapp.config

import com.mayosen.financeapp.readmodel.transactionhistory.jdbc.TransactionViewEntity
import com.mayosen.financeapp.readmodel.transactionhistory.jdbc.TransactionViewEntityRowMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.jdbc.repository.QueryMappingConfiguration
import org.springframework.data.jdbc.repository.config.DefaultQueryMappingConfiguration

@Configuration
class DataJdbcConfiguration {
    @Bean
    fun rowMappers(): QueryMappingConfiguration =
        DefaultQueryMappingConfiguration()
            .registerRowMapper(TransactionViewEntity::class.java, TransactionViewEntityRowMapper)
}
