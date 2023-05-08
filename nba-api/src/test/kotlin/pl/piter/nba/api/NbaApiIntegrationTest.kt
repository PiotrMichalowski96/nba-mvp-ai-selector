package pl.piter.nba.api

import com.ninjasquad.springmockk.MockkBean
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import pl.piter.nba.api.config.TestProducerConfig
import pl.piter.nba.api.config.TopicProperties
import pl.piter.nba.api.rest.NbaScoresProviderApiClient
import pl.piter.nba.api.util.MockedExternalAPI

@SpringBootTest(classes = [TestProducerConfig::class], webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("INT-TEST")
@DirtiesContext
@EmbeddedKafka(
    partitions = 1,
    brokerProperties = ["listeners=PLAINTEXT://localhost:9092", "port=9092"]
)
class NbaApiIntegrationTest(
    @Autowired private val kafkaTemplate: KafkaTemplate<String, String>,
    @Autowired private val topicProperties: TopicProperties,
    @Autowired private val testRestTemplate: TestRestTemplate
) {

    @MockkBean
    private lateinit var apiClient: NbaScoresProviderApiClient

    private lateinit var mockedExternalAPI: MockedExternalAPI

    @BeforeEach
    fun init() {
        mockedExternalAPI = MockedExternalAPI(apiClient)
    }

    @Test
    fun `given mvp event on topic when call get mvp by id then return nba game mvp`() {
        //given

        //when

        //then

    }

    @Test
    fun `given mvp event is not present on topic when call get mvp by id then return not found`() {
        //given

        //when

        //then

    }

    @Test
    fun `given mvp event is already present on topic when call post mvp then return ok status`() {
        //given

        //when

        //then

    }

    @Test
    fun `given game id when call post to create mvp by id then produce nba game event on topic`() {
        //given

        //when

        //then

    }
}