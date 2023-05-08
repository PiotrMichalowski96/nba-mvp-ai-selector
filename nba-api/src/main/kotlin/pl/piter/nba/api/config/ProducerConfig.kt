package pl.piter.nba.api.config

import org.apache.kafka.clients.producer.ProducerConfig.*
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import org.springframework.kafka.support.serializer.JsonSerializer
import pl.piter.commons.domain.NbaGameEvent

@Configuration
class ProducerConfig(@Value("\${spring.kafka.bootstrap-servers}") private val bootstrapAddress: String) {

    @Bean
    fun producerFactory(): ProducerFactory<String, NbaGameEvent> {
        val properties: Map<String, Any> = mapOf(
            BOOTSTRAP_SERVERS_CONFIG to bootstrapAddress,
            KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java.name,
            VALUE_SERIALIZER_CLASS_CONFIG to JsonSerializer::class.java.name,
            ENABLE_IDEMPOTENCE_CONFIG to "true",
            ACKS_CONFIG to "all",
            RETRIES_CONFIG to Int.MAX_VALUE,
            MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION to "5"
        )
        return DefaultKafkaProducerFactory(properties)
    }

    @Bean
    fun kafkaTemplate(): KafkaTemplate<String, NbaGameEvent> = KafkaTemplate(producerFactory())
}