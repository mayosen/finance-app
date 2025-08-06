package com.mayosen.financeapp.test.context.kafka

import org.apache.kafka.clients.consumer.ConsumerRecord
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit

class KafkaTestConsumer<T> {
    private val records: BlockingQueue<ConsumerRecord<String, T>> = LinkedBlockingQueue()

    fun capture(record: ConsumerRecord<String, T>) {
        records.add(record)
    }

    fun pollNotNull(): ConsumerRecord<String, T> {
        val record = records.poll(2, TimeUnit.SECONDS)!!
        return record
    }
}
