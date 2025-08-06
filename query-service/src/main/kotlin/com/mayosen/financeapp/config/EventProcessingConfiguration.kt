package com.mayosen.financeapp.config

import com.mayosen.financeapp.event.EventHandler
import com.mayosen.financeapp.event.EventSubscriber
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
