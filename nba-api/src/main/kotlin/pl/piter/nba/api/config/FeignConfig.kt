package pl.piter.nba.api.config

import feign.Logger
import feign.RequestInterceptor
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class FeignConfig(@Value("\${nba-scores-provider.api.key}") private val apiKey: String) {

    @Bean
    fun authInterceptor(): RequestInterceptor = RequestInterceptor {
        it.query("api_key", apiKey)
    }

    @Bean
    fun logger(): Logger.Level = Logger.Level.FULL
}