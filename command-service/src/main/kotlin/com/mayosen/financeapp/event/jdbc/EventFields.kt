package com.mayosen.financeapp.event.jdbc

import com.fasterxml.jackson.databind.node.ObjectNode

data class EventFields(
    val tree: ObjectNode,
)
