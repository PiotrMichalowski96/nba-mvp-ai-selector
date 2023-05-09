package pl.piter.nba.api.producer

import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import pl.piter.commons.domain.NbaGameEvent
import pl.piter.nba.api.config.TopicProperties

@Component
class NbaGameProducer(
    private val topicProperties: TopicProperties,
    private val kafkaTemplate: KafkaTemplate<String, NbaGameEvent>
) {

    companion object {
        @JvmStatic
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = LoggerFactory.getLogger(javaClass.enclosingClass)
    }

    fun send(nbaGameEvent: NbaGameEvent) {
        val topic: String = topicProperties.match
        val key: String = nbaGameEvent.gameId
        logger.info("Sending game event=$nbaGameEvent and key=$key to topic=$topic")

        kafkaTemplate.send(topic, key, nbaGameEvent)
            .whenComplete { result, ex ->
                if (ex == null) {
                    logger.info("Sent with offset=${result.recordMetadata.offset()}")
                } else {
                    logger.info("Unable to send message due to: ${ex.message}")
                }
            }
    }
}