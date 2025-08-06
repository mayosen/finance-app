package com.mayosen.financeapp.util.serialization

import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import java.sql.Timestamp
import java.time.Instant
import java.time.ZoneOffset

@ReadingConverter
object TimestampToInstantConverter : Converter<Timestamp, Instant> {
    override fun convert(source: Timestamp): Instant = source.toLocalDateTime().toInstant(ZoneOffset.UTC)
}
