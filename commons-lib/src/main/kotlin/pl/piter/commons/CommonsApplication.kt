package pl.piter.commons

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CommonsApplication

//TODO: testing Sonar Cloud

fun main(args: Array<String>) {
    runApplication<CommonsApplication>(*args)
}
