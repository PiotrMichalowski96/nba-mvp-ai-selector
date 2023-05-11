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

        val key = nbaGameEvent.gameId

        mockExternalChatAPI(nbaGameEvent)

        //when
        val topologyTestDriver: TopologyTestDriver = initializeTestDriver()

        topologyTestDriver.use {
            val inputTopic: TestInputTopic<String, NbaGameEvent> =
                topologyTestDriver.createInputTopic("match", StringSerializer(), JsonSerializer())

            val outputTopic: TestOutputTopic<String, MvpEvent> =
                topologyTestDriver.createOutputTopic("mvp", StringDeserializer(), JsonDeserializer(MvpEvent::class.java))

            inputTopic.pipeInput(key, nbaGameEvent)

            val record: KeyValue<String, MvpEvent> = outputTopic.readKeyValue()

            //then
            assertThat(record.key).isEqualTo(key)
            assertThat(record.value).isEqualTo(expectedMvpEvent)
        }

    }

    private fun setupTopology() {
        val topicProperties = TopicProperties("match", "mvp")
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

    private fun mockExternalChatAPI(nbaGameEvent: NbaGameEvent) {
        val responseSample = "src/test/resources/responseChatGPT.json"
        val response: ChatGPTResponse = JsonConverter.readJsonFile(responseSample)
        mockedChatGPT.mockAskChatGPT(nbaGameEvent.toChatGPTRequest(), response)
    }
}