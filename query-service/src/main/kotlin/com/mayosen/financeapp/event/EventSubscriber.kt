package com.mayosen.financeapp.event

/**
 * Подписывается на события из Command Side.
 */
interface EventSubscriber {
    fun subscribe(handler: EventHandler)
}
