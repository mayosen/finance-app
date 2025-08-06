package com.mayosen.financeapp.event

import org.springframework.beans.factory.InitializingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class EventProcessingConfiguration {
    @Bean
    fun eventSubscription(
        eventSubscriber: EventSubscriber,
        eventHandler: EventHandler,
    ): InitializingBean =
        InitializingBean {
            eventSubscriber.subscribe(eventHandler)
        }
}
