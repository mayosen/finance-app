package com.mayosen.financeapp

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CommandApplication

fun main(args: Array<String>) {
    runApplication<CommandApplication>(*args)
}
