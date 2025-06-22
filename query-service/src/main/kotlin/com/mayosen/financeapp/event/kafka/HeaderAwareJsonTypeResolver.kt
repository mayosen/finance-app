package com.mayosen.financeapp.event.kafka

import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.ObjectMapper
import com.mayosen.financeapp.event.serialization.EVENT_TYPE_KEY
import org.apache.kafka.common.header.Headers
import org.springframework.kafka.support.serializer.JsonTypeResolver

class HeaderAwareJsonTypeResolver(
    private val objectMapper: ObjectMapper,
) : JsonTypeResolver {
    override fun resolveType(
        topic: String,
        data: ByteArray,
        headers: Headers,
    ): JavaType {
        // TODO: Throw if header is null
        val typeHeader =
            headers.lastHeader(EVENT_TYPE_KEY)
                ?: throw IllegalArgumentException("Event event type header $EVENT_TYPE_KEY not found")
        val typeValue = String(typeHeader.value(), Charsets.UTF_8)
        val type = Class.forName(typeValue)
        return objectMapper.constructType(type)
    }
}
