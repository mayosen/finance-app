package com.mayosen.financeapp.event

interface EventHandler {
    fun handle(event: Event)
}
