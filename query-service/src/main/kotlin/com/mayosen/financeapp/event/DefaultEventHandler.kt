package com.mayosen.financeapp.event

import org.springframework.stereotype.Component

@Component
class DefaultEventHandler(
    private val serviceEventProcessor: ServiceEventProcessor,
    private val accountProjector: AccountProjector,
) : EventHandler {
    override fun handle(event: Event) {
        if (event is ServiceEvent) {
            serviceEventProcessor.process(event)
        } else {
            accountProjector.project(event)
        }
    }
}
