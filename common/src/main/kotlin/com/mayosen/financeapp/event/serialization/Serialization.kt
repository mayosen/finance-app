package com.mayosen.financeapp.event.serialization

import com.mayosen.financeapp.event.Event
import kotlin.reflect.KClass

const val EVENT_TYPE_KEY = "type"

fun Event.typeName(): String = this::class.typeName()

fun KClass<out Event>.typeName(): String = qualifiedName!!
