package com.mayosen.financeapp.test.context

import com.fasterxml.jackson.databind.ObjectMapper
import com.mayosen.financeapp.projection.account.jdbc.AccountSummaryEntity
import com.mayosen.financeapp.projection.transaction.jdbc.TransactionEntity
import com.mayosen.financeapp.test.context.initializer.KafkaTestContainerInitializer
import com.mayosen.financeapp.test.context.initializer.PostgresqlTestContainerInitializer
import com.mayosen.financeapp.test.context.kafka.KafkaTestProducerConfiguration
import org.junit.jupiter.api.AfterEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.env.Environment
import org.springframework.data.jdbc.core.JdbcAggregateTemplate
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc

@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest(classes = [TestBeanDefinitions::class, KafkaTestProducerConfiguration::class])
@ContextConfiguration(initializers = [PostgresqlTestContainerInitializer::class, KafkaTestContainerInitializer::class])
class BaseIntegrationTest {
    @Autowired
    protected lateinit var mockMvc: MockMvc

    @Autowired
    protected lateinit var jdbcTemplate: JdbcAggregateTemplate

    @Autowired
    protected lateinit var objectMapper: ObjectMapper

    @Autowired
    protected lateinit var eventKafkaTemplate: KafkaTemplate<String, String>

    @Autowired
    protected lateinit var environment: Environment

    @AfterEach
    fun cleanUp() {
        jdbcTemplate.deleteAll(AccountSummaryEntity::class.java)
        jdbcTemplate.deleteAll(TransactionEntity::class.java)
    }

    protected fun stringBodyOf(body: Any): String = objectMapper.writeValueAsString(body)
}
