package pl.piter.mvp.selector

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class NbaApiApplication

fun main(args: Array<String>) {
    runApplication<NbaApiApplication>(*args)
}
