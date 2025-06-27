package com.mayosen.financeapp.test.context

import com.fasterxml.jackson.databind.ObjectMapper
import com.mayosen.financeapp.event.Event
import com.mayosen.financeapp.event.serialization.EVENT_TYPE_KEY
import com.mayosen.financeapp.event.serialization.typeName
import com.mayosen.financeapp.projection.account.jdbc.AccountSummaryEntity
import com.mayosen.financeapp.projection.transaction.jdbc.TransactionEntity
import com.mayosen.financeapp.test.context.initializer.KafkaTestContainerInitializer
import com.mayosen.financeapp.test.context.initializer.PostgresqlTestContainerInitializer
import com.mayosen.financeapp.test.context.kafka.KafkaTestProducerConfiguration
import com.mayosen.financeapp.test.identifier.TestIdGenerator
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.header.Header
import org.apache.kafka.common.header.internals.RecordHeader
import org.awaitility.kotlin.atMost
import org.awaitility.kotlin.await
import org.awaitility.kotlin.untilAsserted
import org.awaitility.kotlin.withPollDelay
import org.junit.jupiter.api.AfterEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.jdbc.core.JdbcAggregateTemplate
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import java.time.Duration

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
    protected lateinit var idGenerator: TestIdGenerator

    @Value("\${app.kafka.events.topic}")
    private lateinit var eventsTopic: String

    @AfterEach
    fun cleanUp() {
        jdbcTemplate.deleteAll(AccountSummaryEntity::class.java)
        jdbcTemplate.deleteAll(TransactionEntity::class.java)
        idGenerator.reset()
        // TODO: Clean Kafka queue
    }

    protected fun stringBodyOf(body: Any): String = objectMapper.writeValueAsString(body)

    protected fun Event.toProducerRecord(): ProducerRecord<String, String> {
        val payload = objectMapper.writeValueAsString(this)
        val headers = listOf(createHeader(EVENT_TYPE_KEY, typeName()))
        return ProducerRecord(eventsTopic, null, eventId, payload, headers)
    }

    protected fun createHeader(
        key: String,
        value: String,
    ): Header = RecordHeader(key, value.toByteArray())

    protected fun baseAwait(
        timeout: Int,
        action: () -> Unit,
    ) = baseAwait(Duration.ofSeconds(timeout.toLong()), action)

    protected fun baseAwait(
        timeout: Duration = SECONDS_1,
        action: () -> Unit,
    ) = await atMost timeout withPollDelay MILLIS_200 untilAsserted action

    protected companion object {
        val SECONDS_1: Duration = Duration.ofSeconds(1)
        val MILLIS_200: Duration = Duration.ofMillis(200)
    }
}
