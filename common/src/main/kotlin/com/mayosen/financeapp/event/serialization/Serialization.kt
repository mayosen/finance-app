package com.mayosen.financeapp.event.serialization

import com.mayosen.financeapp.event.Event
import kotlin.reflect.KClass

typealias EventType = KClass<out Event>

const val EVENT_TYPE_KEY = "type"

fun Event.typeName(): String = this::class.typeName()

fun EventType.typeName(): String = qualifiedName!!
