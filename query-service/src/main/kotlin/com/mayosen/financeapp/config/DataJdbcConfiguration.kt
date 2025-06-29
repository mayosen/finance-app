package com.mayosen.financeapp.config

import com.mayosen.financeapp.projection.transaction.jdbc.TransactionEntity
import com.mayosen.financeapp.projection.transaction.jdbc.TransactionEntityRowMapper
import com.mayosen.financeapp.util.serialization.InstantToTimestampConverter
import com.mayosen.financeapp.util.serialization.TimestampToInstantConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.jdbc.core.convert.JdbcCustomConversions
import org.springframework.data.jdbc.repository.QueryMappingConfiguration
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration
import org.springframework.data.jdbc.repository.config.DefaultQueryMappingConfiguration

@Configuration
class DataJdbcConfiguration : AbstractJdbcConfiguration() {
    @Bean
    fun rowMappers(): QueryMappingConfiguration =
        DefaultQueryMappingConfiguration()
            .registerRowMapper(TransactionEntity::class.java, TransactionEntityRowMapper)

    override fun jdbcCustomConversions(): JdbcCustomConversions =
        JdbcCustomConversions(
            listOf(
                InstantToTimestampConverter,
                TimestampToInstantConverter,
            ),
        )
}
