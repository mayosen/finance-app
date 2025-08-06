package com.mayosen.financeapp.event.jdbc.serialization.rules

import com.fasterxml.jackson.databind.node.ObjectNode
import com.mayosen.financeapp.event.jdbc.EventEntity

interface ExplicitFieldRule<T> {
    fun writeTo(
        event: EventEntity,
        tree: ObjectNode,
    )

    fun removeFrom(tree: ObjectNode)
}
