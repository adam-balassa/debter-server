package hu.balassa.debter

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
open class DebterApplication

fun main(args: Array<String>) {
    runApplication<DebterApplication>(*args)
}
