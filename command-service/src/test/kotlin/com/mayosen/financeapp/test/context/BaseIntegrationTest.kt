package com.mayosen.financeapp.test.context

import com.fasterxml.jackson.databind.ObjectMapper
import com.mayosen.financeapp.event.Event
import com.mayosen.financeapp.test.context.initializer.KafkaTestContainerInitializer
import com.mayosen.financeapp.test.context.initializer.PostgresqlTestContainerInitializer
import com.mayosen.financeapp.test.context.kafka.KafkaTestConsumer
import com.mayosen.financeapp.test.context.kafka.KafkaTestConsumerConfiguration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.jdbc.core.JdbcAggregateTemplate
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc

@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest(classes = [TestBeanDefinitions::class, KafkaTestConsumerConfiguration::class])
@ContextConfiguration(initializers = [PostgresqlTestContainerInitializer::class, KafkaTestContainerInitializer::class])
class BaseIntegrationTest {
    @Autowired
    protected lateinit var mockMvc: MockMvc

    @Autowired
    protected lateinit var jdbcTemplate: JdbcAggregateTemplate

    @Autowired
    protected lateinit var objectMapper: ObjectMapper

    @Autowired
    protected lateinit var eventsCustomer: KafkaTestConsumer<Event>

    protected fun stringBodyOf(body: Any): String = objectMapper.writeValueAsString(body)
}
