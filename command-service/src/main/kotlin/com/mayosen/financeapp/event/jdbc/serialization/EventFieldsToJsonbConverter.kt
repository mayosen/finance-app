package com.mayosen.financeapp.event.jdbc.serialization

import com.fasterxml.jackson.databind.ObjectMapper
import com.mayosen.financeapp.event.jdbc.EventFields
import org.postgresql.util.PGobject
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.WritingConverter

@WritingConverter
class EventFieldsToJsonbConverter(
    private val objectMapper: ObjectMapper,
) : Converter<EventFields, PGobject> {
    override fun convert(source: EventFields): PGobject {
        val stringValue =
            try {
                objectMapper.writeValueAsString(source)
            } catch (e: Exception) {
                throw IllegalStateException("Failed to serialize EventFields as JSON string", e)
            }
        return PGobject().apply {
            type = "jsonb"
            value = stringValue
        }
    }
}
