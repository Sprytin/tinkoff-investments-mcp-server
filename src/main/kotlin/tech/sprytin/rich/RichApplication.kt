package tech.sprytin.rich

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class RichApplication

fun main(args: Array<String>) {
    runApplication<RichApplication>(*args)
}
