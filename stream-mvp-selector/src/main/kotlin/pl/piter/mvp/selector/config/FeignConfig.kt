package pl.piter.mvp.selector.config

import feign.Logger
import feign.RequestInterceptor
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class FeignConfig(@Value("\${chat-gpt.api.key}") private val apiKey: String) {

    companion object {
        private const val HEADER_AUTHORIZATION = "Authorization"
    }

    @Bean
    fun authInterceptor(): RequestInterceptor = RequestInterceptor {
        it.header(HEADER_AUTHORIZATION, "Bearer $apiKey")
    }

    @Bean
    fun logger(): Logger.Level = Logger.Level.FULL
}