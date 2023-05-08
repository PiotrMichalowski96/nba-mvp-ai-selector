package pl.piter.nba.api.config

import org.apache.kafka.clients.admin.AdminClientConfig
import org.apache.kafka.clients.admin.NewTopic
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.TopicBuilder
import org.springframework.kafka.core.KafkaAdmin

@Configuration
class TopicConfig(
    @Value("\${spring.kafka.bootstrap-servers}") private val bootstrapAddress: String,
    private val topicProperties: TopicProperties
) {

    @Bean
    fun kafkaAdmin(): KafkaAdmin {
        val properties = mutableMapOf<String, Any>()
        properties[AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapAddress
        return KafkaAdmin(properties)
    }

    @Bean
    fun matchTopic(): NewTopic = TopicBuilder.name(topicProperties.match)
        .partitions(12)
        .replicas(1)
        .compact()
        .build()

    @Bean
    fun mvpTopic(): NewTopic = TopicBuilder.name(topicProperties.mvp)
        .partitions(12)
        .replicas(1)
        .compact()
        .build()
}