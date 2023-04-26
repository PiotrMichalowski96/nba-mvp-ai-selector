package pl.piter.nba.api.config

import feign.RequestInterceptor
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders

@Configuration
class FeignConfig(@Value("#{sports-radar.api.key}") private val apiKey: String) {

    @Bean
    fun authInterceptor(): RequestInterceptor {
        return RequestInterceptor {
            it.header(HttpHeaders.AUTHORIZATION, "Bearer $apiKey")
        }
    }
}