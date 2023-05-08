package pl.piter.nba.api.config

import io.mockk.mockk
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.kafka.config.StreamsBuilderFactoryBean
import pl.piter.nba.api.producer.NbaGameProducer
import pl.piter.nba.api.service.NbaGameService
import pl.piter.nba.api.service.ScoreApiService
import pl.piter.nba.api.validation.AnnotationValidator

@TestConfiguration
@Import(NbaGameService::class, ScoreApiService::class, AnnotationValidator::class)
class TestConfig {

    @Bean
    fun mockStreamsBuilderFactoryBean(): StreamsBuilderFactoryBean =
        mockk<StreamsBuilderFactoryBean>(relaxed = true)

    @Bean
    fun mockKafkaProducer(): NbaGameProducer = mockk<NbaGameProducer>(relaxed = true)
}