package com.mayosen.financeapp.event.jdbc.serialization

import com.fasterxml.jackson.databind.ObjectMapper
import com.mayosen.financeapp.event.jdbc.EventFields
import org.postgresql.util.PGobject
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter

@ReadingConverter
class JsonbToEventFieldsConverter(
    private val objectMapper: ObjectMapper,
) : Converter<PGobject, EventFields> {
    override fun convert(source: PGobject): EventFields =
        try {
            require(source.type == "jsonb")
            objectMapper.readValue(source.value!!, EventFields::class.java)
        } catch (e: Exception) {
            throw IllegalStateException("Failed to deserialize JSONB from PGobject", e)
        }
}
