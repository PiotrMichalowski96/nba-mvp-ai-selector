package pl.piter.nba.api

import com.ninjasquad.springmockk.MockkBean
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.ConsumerRecords
import org.apache.kafka.common.serialization.StringDeserializer
import org.assertj.core.api.Assertions.assertThat
import org.awaitility.kotlin.await
import org.awaitility.kotlin.matches
import org.awaitility.kotlin.untilCallTo
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.skyscreamer.jsonassert.JSONAssert
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.*
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.test.EmbeddedKafkaBroker
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.kafka.test.utils.KafkaTestUtils
import org.springframework.test.context.ActiveProfiles
import pl.piter.commons.api.model.scores.GameResponse
import pl.piter.commons.util.JsonConverter
import pl.piter.commons.util.generateId
import pl.piter.nba.api.config.TestProducerConfig
import pl.piter.nba.api.config.TopicProperties
import pl.piter.nba.api.rest.NbaScoresProviderApiClient
import pl.piter.nba.api.util.MockedExternalAPI
import pl.piter.nba.api.util.RedisExtension
import java.time.Duration
import java.util.Collections.singleton

@ExtendWith(RedisExtension::class)
@SpringBootTest(
    classes = [TestProducerConfig::class],
    webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT
)
@ActiveProfiles("INT-TEST")
@EmbeddedKafka(
    partitions = 12,
    brokerProperties = ["listeners=PLAINTEXT://localhost:9092", "port=9092"]
)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class NbaApiIntegrationTest(
    @Autowired private val kafkaTemplate: KafkaTemplate<String, String>,
    @Autowired private val topicProperties: TopicProperties,
    @Autowired private val testRestTemplate: TestRestTemplate,
    @Autowired private val embeddedKafka: EmbeddedKafkaBroker
) {

    @MockkBean
    private lateinit var apiClient: NbaScoresProviderApiClient

    private lateinit var mockedExternalAPI: MockedExternalAPI

    private lateinit var consumer: Consumer<String, String>

    @BeforeAll
    fun setup() {
        mockedExternalAPI = MockedExternalAPI(apiClient)
        setupKafkaConsumer()
    }

    @Test
    fun `given mvp event is present on topic when call get mvp by id then return nba game mvp`() {
        //given
        val expectedMvpSample = "src/test/resources/nbaGameMvp.json"
        val expectedMvp = JsonConverter.readFileAsString(expectedMvpSample)

        val id: String = generateId(20)
        sendMvpEvent(id)

        //when
        val response: ResponseEntity<String>? = await.timeout(Duration.ofMinutes(1))
            .untilCallTo { callGetMvp(id) } matches { it?.statusCode?.is2xxSuccessful ?: false }

        //then
        assertThat(response?.statusCode?.value()).isEqualTo(HttpStatus.OK.value())
        JSONAssert.assertEquals(expectedMvp, response?.body, false)
    }

    @Test
    fun `given mvp event is not present on topic when call get mvp by id then return not found`() {
        //given
        val nonExistingId: String = generateId(20)

        //when
        val response: ResponseEntity<String>? = await.timeout(Duration.ofMinutes(1))
            .untilCallTo { callGetMvp(nonExistingId) } matches {
            it?.statusCode?.is4xxClientError ?: false
        }

        //then
        assertThat(response?.statusCode?.value()).isEqualTo(HttpStatus.NOT_FOUND.value())
    }

    @Test
    fun `given mvp event is already present on topic when call post mvp then return ok status`() {
        //given
        val id: String = generateId(20)
        sendMvpEvent(id)

        //when
        val response: ResponseEntity<String>? = await.timeout(Duration.ofMinutes(1))
            .untilCallTo { callPostMvp(id) } matches { it?.statusCode?.is2xxSuccessful ?: false }

        //then
        assertThat(response?.statusCode?.value()).isEqualTo(HttpStatus.OK.value())
    }

    @Test
    fun `given game id when call post to create mvp by id then produce nba game event on topic`() {
        //given
        val gameResponseSample = "src/test/resources/gameById.json"
        val gameResponse: GameResponse = JsonConverter.readJsonFile(gameResponseSample)

        val id: String = gameResponse.id
        mockedExternalAPI.mockGetGameEndpoint(id, gameResponse)

        val expectedGameEventSample = "src/test/resources/nbaGameEvent.json"
        val expectedGameEvent = JsonConverter.readFileAsString(expectedGameEventSample)

        //when
        val response: ResponseEntity<String>? = await.timeout(Duration.ofMinutes(1))
            .untilCallTo { callPostMvp(id) } matches { it?.statusCode?.is2xxSuccessful ?: false }

        //then
        assertThat(response?.statusCode?.value()).isEqualTo(HttpStatus.OK.value())
        assertRecord(id, expectedGameEvent)
    }

    private fun setupKafkaConsumer() {
        val configs: Map<String, Any> = KafkaTestUtils.consumerProps(
            "consumer",
            "false",
            embeddedKafka
        )
        consumer = DefaultKafkaConsumerFactory(
            configs,
            StringDeserializer(),
            StringDeserializer()
        ).createConsumer()
        consumer.subscribe(singleton(topicProperties.match))
    }

    private fun sendMvpEvent(id: String) {
        val eventSample = "src/test/resources/mvpEvent.json"
        val mvpEventJson = JsonConverter.readFileAsString(eventSample)
        kafkaTemplate.send(topicProperties.mvp, id, mvpEventJson)
    }

    private fun callMvpEndpoint(httpMethod: HttpMethod, id: String): ResponseEntity<String> =
        testRestTemplate.exchange(
            "http://localhost:8081/nba-game/$id/mvp",
            httpMethod,
            HttpEntity<String>(HttpHeaders()),
            String::class.java
        )

    private fun callGetMvp(id: String): ResponseEntity<String> = callMvpEndpoint(HttpMethod.GET, id)

    private fun callPostMvp(id: String): ResponseEntity<String> = callMvpEndpoint(HttpMethod.POST, id)

    private fun assertRecord(kafkaKey: String, expectedJson: String) {
        val records: ConsumerRecords<String, String> = KafkaTestUtils.getRecords(consumer)

        val record: ConsumerRecord<String, String>? = records.records(topicProperties.match)
            .toList()
            .findLast { it.key() == kafkaKey }

        JSONAssert.assertEquals(expectedJson, record?.value(), false)
    }
}