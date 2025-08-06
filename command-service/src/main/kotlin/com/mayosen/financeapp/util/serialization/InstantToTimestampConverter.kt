package com.mayosen.financeapp.util.serialization

import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.WritingConverter
import java.sql.Timestamp
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

@WritingConverter
object InstantToTimestampConverter : Converter<Instant, Timestamp> {
    override fun convert(source: Instant): Timestamp = Timestamp.valueOf(LocalDateTime.ofInstant(source, ZoneOffset.UTC))
}
