package pl.piter.mvp.selector

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients

@EnableFeignClients(basePackages = ["pl.piter.mvp.selector.rest"])
@SpringBootApplication
class NbaApiApplication

fun main(args: Array<String>) {
    runApplication<NbaApiApplication>(*args)
}
