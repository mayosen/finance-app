package com.mayosen.financeapp.test.context

import com.mayosen.financeapp.test.identifier.TestIdGenerator
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary

@TestConfiguration
class TestBeanDefinitions {
    @Primary
    @Bean
    fun testIdGenerator(): TestIdGenerator = TestIdGenerator
}
