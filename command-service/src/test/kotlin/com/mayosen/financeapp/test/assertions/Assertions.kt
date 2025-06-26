package com.mayosen.financeapp.test.assertions

import com.mayosen.financeapp.event.Event
import com.mayosen.financeapp.event.serialization.EVENT_TYPE_KEY
import com.mayosen.financeapp.event.serialization.EventType
import com.mayosen.financeapp.event.serialization.typeName
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.header.Headers
import org.assertj.core.api.AbstractInstantAssert
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.byLessThan
import java.time.Instant
import java.time.temporal.ChronoUnit
import kotlin.reflect.KClass

fun Headers.assertHasType(type: EventType) = assertHas(EVENT_TYPE_KEY, type.typeName())

fun Headers.assertHas(
    key: String,
    value: String,
) {
    val header = lastHeader(key)
    assertThat(header).isNotNull

    val deserializedValue = String(header.value())
    assertThat(deserializedValue).isEqualTo(value)
}

@Suppress("UNCHECKED_CAST")
fun <KEY, VALUE, TYPE : Event> ConsumerRecord<KEY, VALUE>.assertHasValueOfType(type: KClass<TYPE>): TYPE {
    assertThat(value()).isExactlyInstanceOf(type.java)
    return value() as TYPE
}

fun AbstractInstantAssert<*>.isCloseToNow(seconds: Int = 1): AbstractInstantAssert<*> =
    isCloseTo(
        Instant.now(),
        byLessThan(seconds.toLong(), ChronoUnit.SECONDS),
    )
