package hu.balassa.debter

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching

@SpringBootApplication
open class DebterApplication

fun main(args: Array<String>) {
    runApplication<DebterApplication>(*args)
}
