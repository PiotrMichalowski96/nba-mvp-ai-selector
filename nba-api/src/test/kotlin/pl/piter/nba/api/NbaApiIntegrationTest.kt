package pl.piter.nba.api

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import pl.piter.nba.api.config.TestProducerConfig

@SpringBootTest(classes = [TestProducerConfig::class])
@ActiveProfiles("INT-TEST")
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = ["listeners=PLAINTEXT://localhost:9092", "port=9092"])
class NbaApiIntegrationTest(@Autowired private val kafkaTemplate: KafkaTemplate<String, String>) {

    @Test
    fun `given when then`() {

    }
}