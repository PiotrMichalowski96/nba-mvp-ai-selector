package pl.piter.commons

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CommonsApplication

fun main(args: Array<String>) {
    runApplication<CommonsApplication>(*args)
}
