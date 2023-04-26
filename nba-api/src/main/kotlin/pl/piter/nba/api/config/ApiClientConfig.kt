package pl.piter.nba.api.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import pl.piter.nba.api.rest.*

@Configuration
class ApiClientConfig() {

    @Bean
    fun cachingLoggingApiClientService(apiClient: SportRadarNbaApiClient): ApiClientService {
        val defaultService = DefaultApiClientService(apiClient)
        val loggingService = LoggingApiClientService(defaultService)
        return CachingApiClientService(loggingService)
    }
}