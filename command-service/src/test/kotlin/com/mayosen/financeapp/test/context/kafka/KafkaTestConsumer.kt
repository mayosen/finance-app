package com.mayosen.financeapp.test.context.kafka

import org.apache.kafka.clients.consumer.ConsumerRecord
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue

class KafkaTestConsumer<T> {
    val records: BlockingQueue<ConsumerRecord<String, T>> = LinkedBlockingQueue()

    fun capture(record: ConsumerRecord<String, T>) {
        records.add(record)
    }
}
