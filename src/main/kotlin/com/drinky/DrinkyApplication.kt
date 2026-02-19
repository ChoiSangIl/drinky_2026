package com.drinky

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class DrinkyApplication

fun main(args: Array<String>) {
    runApplication<DrinkyApplication>(*args)
}
