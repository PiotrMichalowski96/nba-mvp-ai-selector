package pl.piter.nba.api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.context.annotation.Import
import pl.piter.commons.config.CommonsConfig

@EnableFeignClients(basePackages = ["pl.piter.nba.api.rest"])
@Import(CommonsConfig::class)
@SpringBootApplication
class NbaApiApplication

fun main(args: Array<String>) {
    runApplication<NbaApiApplication>(*args)
}
