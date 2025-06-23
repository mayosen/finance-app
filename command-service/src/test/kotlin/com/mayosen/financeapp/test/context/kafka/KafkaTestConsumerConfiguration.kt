package com.mayosen.financeapp.test.context.kafka

import com.fasterxml.jackson.databind.ObjectMapper
import com.mayosen.financeapp.event.Event
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.Deserializer
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.core.env.Environment
import org.springframework.core.env.get
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.listener.ContainerProperties
import org.springframework.kafka.listener.KafkaMessageListenerContainer
import org.springframework.kafka.listener.MessageListener
import org.springframework.kafka.listener.MessageListenerContainer
import org.springframework.kafka.support.TopicPartitionOffset
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.springframework.kafka.support.serializer.JsonTypeResolver
import com.mayosen.financeapp.event.Event as EventDto

@TestConfiguration
class KafkaTestConsumerConfiguration {
    @Autowired
    protected lateinit var environment: Environment

    @Autowired
    protected lateinit var objectMapper: ObjectMapper

    @Bean
    fun eventsCustomer(): KafkaTestConsumer<EventDto> = KafkaTestConsumer()

    @Bean
    fun headerAwareJsonTypeResolver(): HeaderAwareJsonTypeResolver = HeaderAwareJsonTypeResolver(objectMapper)

    @Bean
    fun eventsMessageListenerContainer(
        headerAwareJsonTypeResolver: HeaderAwareJsonTypeResolver,
        eventsConsumer: KafkaTestConsumer<EventDto>,
    ): MessageListenerContainer {
        val consumerFactory: ConsumerFactory<String, EventDto> = createConsumerFactory(headerAwareJsonTypeResolver)
        val messageListener: MessageListener<String, EventDto> = MessageListener(eventsConsumer::capture)
        return createMessageListenerContainer(consumerFactory, messageListener)
    }

    private fun createConsumerFactory(typeResolver: JsonTypeResolver): ConsumerFactory<String, EventDto> {
        val consumerConfig = getConsumerConfig()
        val keyDeserializer = StringDeserializer()
        val valueDeserializer = getValueDeserializer(typeResolver)
        return DefaultKafkaConsumerFactory(consumerConfig, keyDeserializer, valueDeserializer)
    }

    private fun getConsumerConfig(): Map<String, Any> {
        val props: MutableMap<String, Any> = HashMap()
        props[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = environment["spring.kafka.bootstrap-servers"]!!
        props[ConsumerConfig.GROUP_ID_CONFIG] = "test-consumer-group"
        props[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "earliest"
        return props
    }

    private fun getValueDeserializer(typeResolver: JsonTypeResolver): Deserializer<EventDto> {
        val jsonDeserializer =
            JsonDeserializer(EventDto::class.java, objectMapper).apply {
                setTypeResolver(typeResolver)
                addTrustedPackages(Event::class.java.packageName)
            }
        return ErrorHandlingDeserializer(jsonDeserializer)
    }

    private fun <K, V> createMessageListenerContainer(
        consumerFactory: ConsumerFactory<K, V>,
        messageListener: MessageListener<K, V>,
    ): MessageListenerContainer {
        val topic = "financeapp-events"
        val topicPartitionOffset = TopicPartitionOffset(topic, 0, 0L)
        val containerProperties = ContainerProperties(topicPartitionOffset)
        val messageListenerContainer =
            KafkaMessageListenerContainer(consumerFactory, containerProperties)
                .apply { setupMessageListener(messageListener) }
        return messageListenerContainer
    }
}
