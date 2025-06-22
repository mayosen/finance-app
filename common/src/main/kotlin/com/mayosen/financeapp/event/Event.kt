package com.mayosen.financeapp.event

import java.time.Instant
import kotlin.reflect.KClass

interface Event {
    val eventId: String
    val accountId: String
    val timestamp: Instant
}

// TODO: Move if not used in query service

fun Event.typeName(): String = this::class.typeName()

fun KClass<out Event>.typeName(): String = qualifiedName!!

const val EVENT_TYPE_KEY = "type"
