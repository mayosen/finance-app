package com.mayosen.financeapp.event.jdbc.serialization.rules

import com.fasterxml.jackson.databind.node.ObjectNode
import com.mayosen.financeapp.event.jdbc.EventEntity

class StringExplicitFieldRule(
    private val key: String,
    private val valueSelector: (EventEntity) -> String,
) : ExplicitFieldRule<String> {
    override fun writeTo(
        event: EventEntity,
        tree: ObjectNode,
    ) {
        val value = valueSelector.invoke(event)
        tree.put(key, value)
    }

    override fun removeFrom(tree: ObjectNode) {
        tree.remove(key)
    }
}
