package com.mayosen.financeapp.test.context

import com.mayosen.financeapp.test.util.TestIdGenerator
import com.mayosen.financeapp.util.IdGenerator
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary

@TestConfiguration
class TestBeanDefinitions {
    @Primary
    @Bean
    fun testIdGenerator(): IdGenerator = TestIdGenerator
}
