package pl.piter.mvp.selector.stream

import com.ninjasquad.springmockk.MockkBean
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.apache.kafka.streams.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.springframework.kafka.support.serializer.JsonSerializer
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.validation.Validator
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean
import pl.piter.commons.api.model.chatgpt.ChatGPTResponse
import pl.piter.commons.domain.MvpEvent
import pl.piter.commons.domain.NbaGameEvent
import pl.piter.commons.util.JsonConverter
import pl.piter.commons.validation.AnnotationValidator
import pl.piter.mvp.selector.config.TopicProperties
import pl.piter.mvp.selector.mapper.toChatGPTRequest
import pl.piter.mvp.selector.rest.ChatGPTApiClient
import pl.piter.mvp.selector.service.ChatGPTApiService
import pl.piter.mvp.selector.util.MockedChatGPT

@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [LocalValidatorFactoryBean::class])
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SelectorMvpTopologyTest {

    companion object {
        private const val MVP_TOPIC = "mvp"
        private const val MATCH_TOPIC = "match"
    }

    private lateinit var selectorTopology: SelectorMvpTopology

    @Autowired
    private lateinit var validator: Validator

    @MockkBean
    private lateinit var apiClient: ChatGPTApiClient

    private lateinit var mockedChatGPT: MockedChatGPT

    @BeforeAll
    fun setup() {
        mockedChatGPT = MockedChatGPT(apiClient)
        setupTopology()
    }

    @Test
    fun `given nba game event on input topic when processed then return mvp event`() {
        //given
        val nbaGameSample = "src/test/resources/nbaGameEvent.json"
        val nbaGameEvent: NbaGameEvent = JsonConverter.readJsonFile(nbaGameSample)

        val expectedMvpSample = "src/test/resources/mvpEvent.json"
        val expectedMvpEvent: MvpEvent = JsonConverter.readJsonFile(expectedMvpSample)

        val responseSample = "src/test/resources/responseChatGPT.json"
        val response: ChatGPTResponse = JsonConverter.readJsonFile(responseSample)

        mockedChatGPT.mockAskChatGPT(nbaGameEvent.toChatGPTRequest(), response)

        val assertMvpEvent: OutputTopicAssertion = {
            val record: KeyValue<String, MvpEvent> = it.readKeyValue()
            assertThat(record.key).isEqualTo(nbaGameEvent.gameId)
            assertThat(record.value).isEqualTo(expectedMvpEvent)
        }

        //whenThen
        topologyTestCase(nbaGameEvent, assertMvpEvent)
    }

    @Test
    fun `given invalid nba game event when processed then output topic is empty`() {
        //given
        val invalidGameSample = "src/test/resources/invalidNbaGameEvent.json"
        val invalidNbaGameEvent: NbaGameEvent = JsonConverter.readJsonFile(invalidGameSample)

        //whenThen
        topologyTestCase(invalidNbaGameEvent, assertEmptyTopic)
    }

    @Test
    fun `given invalid response from ChatGPT when processed then output topic is empty`() {
        //given
        val nbaGameSample = "src/test/resources/nbaGameEvent.json"
        val nbaGameEvent: NbaGameEvent = JsonConverter.readJsonFile(nbaGameSample)

        val invalidResponseSample = "src/test/resources/validation/invalidAbsentAnswer.json"
        val invalidResponse: ChatGPTResponse = JsonConverter.readJsonFile(invalidResponseSample)

        mockedChatGPT.mockAskChatGPT(nbaGameEvent.toChatGPTRequest(), invalidResponse)

        //whenThen
        topologyTestCase(nbaGameEvent, assertEmptyTopic)
    }

    private fun topologyTestCase(nbaGameEvent: NbaGameEvent, assertion: OutputTopicAssertion) {
        //given
        val key = nbaGameEvent.gameId

        //when
        val topologyTestDriver: TopologyTestDriver = initializeTestDriver()

        topologyTestDriver.use {
            val inputTopic: TestInputTopic<String, NbaGameEvent> =
                topologyTestDriver.createInputTopic(MATCH_TOPIC, StringSerializer(), JsonSerializer())

            val outputTopic: TestOutputTopic<String, MvpEvent> =
                topologyTestDriver.createOutputTopic(MVP_TOPIC, StringDeserializer(), JsonDeserializer(MvpEvent::class.java))

            inputTopic.pipeInput(key, nbaGameEvent)

            //then
            assertion.invoke(outputTopic)
        }
    }

    private val assertEmptyTopic: OutputTopicAssertion = {
        assertThat(it.isEmpty).isTrue()
    }

    private fun setupTopology() {
        val topicProperties = TopicProperties(MATCH_TOPIC, MVP_TOPIC)
        val annotationValidator = AnnotationValidator(validator)
        val apiService = ChatGPTApiService(apiClient)
        selectorTopology = SelectorMvpTopology(topicProperties, annotationValidator, apiService)
    }

    private fun initializeTestDriver(): TopologyTestDriver {
        val streamsBuilder = StreamsBuilder()
        selectorTopology.buildPipeline(streamsBuilder)
        val topology: Topology =  streamsBuilder.build()
        return TopologyTestDriver(topology)
    }
}

typealias OutputTopicAssertion = (TestOutputTopic<String, MvpEvent>) -> Unit