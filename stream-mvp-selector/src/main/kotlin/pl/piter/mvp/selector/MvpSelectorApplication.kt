package pl.piter.mvp.selector

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.context.annotation.Import
import pl.piter.commons.config.CommonsConfig

@EnableFeignClients(basePackages = ["pl.piter.mvp.selector.rest"])
@Import(CommonsConfig::class)
@SpringBootApplication
class MvpSelectorApplication

fun main(args: Array<String>) {
    runApplication<MvpSelectorApplication>(*args)
}
