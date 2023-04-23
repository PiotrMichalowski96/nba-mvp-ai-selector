package pl.piter.nba.api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class NbaApiApplication

fun main(args: Array<String>) {
    runApplication<NbaApiApplication>(*args)
}
