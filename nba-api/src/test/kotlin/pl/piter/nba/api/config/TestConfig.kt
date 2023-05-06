package pl.piter.nba.api.config

import io.mockk.mockk
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.kafka.config.StreamsBuilderFactoryBean
import pl.piter.nba.api.service.NbaGameService
import pl.piter.nba.api.service.ScoreApiService

@TestConfiguration
@Import(NbaGameService::class, ScoreApiService::class)
class TestConfig {

    @Bean
    fun mockStreamsBuilderFactoryBean(): StreamsBuilderFactoryBean {
        return mockk<StreamsBuilderFactoryBean>(relaxed = true)
    }
}