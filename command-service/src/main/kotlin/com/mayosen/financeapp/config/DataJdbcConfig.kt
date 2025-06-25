package com.mayosen.financeapp.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.mayosen.financeapp.event.jdbc.serialization.EventFieldsToJsonbConverter
import com.mayosen.financeapp.event.jdbc.serialization.JsonbToEventFieldsConverter
import org.springframework.context.annotation.Configuration
import org.springframework.data.jdbc.core.convert.JdbcCustomConversions
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration

@Configuration
class DataJdbcConfig(
    private val objectMapper: ObjectMapper,
) : AbstractJdbcConfiguration() {
    override fun jdbcCustomConversions(): JdbcCustomConversions =
        JdbcCustomConversions(
            listOf(
                EventFieldsToJsonbConverter(objectMapper),
                JsonbToEventFieldsConverter(objectMapper),
            ),
        )
}
