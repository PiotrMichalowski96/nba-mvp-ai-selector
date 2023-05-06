package pl.piter.nba.api.config

import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.StreamsConfig.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.annotation.EnableKafkaStreams
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration
import org.springframework.kafka.config.KafkaStreamsConfiguration
import org.springframework.kafka.support.serializer.JsonSerde

@Configuration
@EnableKafka
@EnableKafkaStreams
class KafkaConfig(@Value("\${spring.kafka.bootstrap-servers}") private val bootstrapAddress: String,
                  @Value("\${spring.application.name}") private val appName: String) {

    @Bean(name = [KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME])
    fun streamsConfig(): KafkaStreamsConfiguration {
        val properties = mutableMapOf<String, Any>()
        properties[BOOTSTRAP_SERVERS_CONFIG] = bootstrapAddress
        properties[APPLICATION_ID_CONFIG] = appName
        properties[DEFAULT_KEY_SERDE_CLASS_CONFIG] = Serdes.String().javaClass.name
        properties[DEFAULT_VALUE_SERDE_CLASS_CONFIG] = JsonSerde::class.java.name
        return KafkaStreamsConfiguration(properties)
    }
}