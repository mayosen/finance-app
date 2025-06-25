package com.mayosen.financeapp.test.context

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import com.mayosen.financeapp.event.Event
import com.mayosen.financeapp.event.jdbc.EventEntity
import com.mayosen.financeapp.event.jdbc.EventFields
import com.mayosen.financeapp.event.jdbc.serialization.EventEntitySerializer
import com.mayosen.financeapp.snapshot.jdbc.AccountSnapshotEntity
import com.mayosen.financeapp.test.SEQUENCE_NUMBER
import com.mayosen.financeapp.test.context.initializer.KafkaTestContainerInitializer
import com.mayosen.financeapp.test.context.initializer.PostgresqlTestContainerInitializer
import com.mayosen.financeapp.test.context.kafka.KafkaTestConsumer
import com.mayosen.financeapp.test.context.kafka.KafkaTestConsumerConfiguration
import com.mayosen.financeapp.test.identifier.TestIdGenerator
import org.junit.jupiter.api.AfterEach
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

    @Autowired
    private lateinit var eventEntitySerializer: EventEntitySerializer

    @Autowired
    protected lateinit var idGenerator: TestIdGenerator

    @AfterEach
    fun cleanUp() {
        jdbcTemplate.deleteAll(EventEntity::class.java)
        jdbcTemplate.deleteAll(AccountSnapshotEntity::class.java)
        idGenerator.reset()
    }

    protected fun stringBodyOf(body: Any): String = objectMapper.writeValueAsString(body)

    protected fun eventFieldsOf(vararg pairs: Pair<String, Any>): EventFields {
        val node = nodeOf(*pairs)
        return EventFields(node)
    }

    private fun nodeOf(vararg pairs: Pair<String, Any>): ObjectNode = nodeOf(pairs.toMap())

    private fun nodeOf(map: Map<String, Any>): ObjectNode = objectMapper.valueToTree(map)

    protected fun eventEntityOf(
        event: Event,
        sequenceNumber: Long = SEQUENCE_NUMBER,
        isNew: Boolean = true,
    ): EventEntity = eventEntitySerializer.serialize(event, sequenceNumber, isNew)

    companion object {
        const val OWNER_ID_FIELD = "ownerId"
        const val AMOUNT_FIELD = "amount"
    }
}
