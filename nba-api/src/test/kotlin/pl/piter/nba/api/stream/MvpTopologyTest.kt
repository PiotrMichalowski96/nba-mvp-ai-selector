package pl.piter.nba.api.stream

import org.apache.kafka.common.serialization.StringSerializer
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.TestInputTopic
import org.apache.kafka.streams.Topology
import org.apache.kafka.streams.TopologyTestDriver
import org.apache.kafka.streams.state.KeyValueStore
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.support.serializer.JsonSerializer
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.validation.Validator
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean
import pl.piter.commons.api.model.nba.NbaGameMvp
import pl.piter.commons.domain.MvpEvent
import pl.piter.commons.util.JsonConverter
import pl.piter.nba.api.config.TopicProperties
import pl.piter.nba.api.validation.AnnotationValidator
import java.util.*

@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [LocalValidatorFactoryBean::class])
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MvpTopologyTest {

    @Autowired
    private lateinit var validator: Validator

    private lateinit var mvpTopology: MvpTopology

    @BeforeAll
    fun init() {
        val topicProperties = TopicProperties("match", "mvp")
        val annotationValidator = AnnotationValidator(validator)
        mvpTopology = MvpTopology(topicProperties, annotationValidator)
    }

    @Test
    fun `given mvp event when processed then mvp model is produced`() {
        //given
        val mvpEventSample = "src/test/resources/mvpEvent.json"

        val expectedMvpSample = "src/test/resources/nbaGameMvp.json"
        val expectedNbaGameMvp: NbaGameMvp = JsonConverter.readJsonFile(expectedMvpSample)

        val assertMvp: MvpAssertion = { mvp -> assertThat(mvp).isEqualTo(expectedNbaGameMvp) }

        //whenThen
        topologyTestCase(mvpEventSample, assertMvp)
    }

    @Test
    fun `given invalid mvp event when processed then result is not produced`() {
        //given
        val invalidSample = "src/test/resources/invalidMvpEvent.json"
        val assertMvpNotProduced: MvpAssertion = { mvp -> assertThat(mvp).isNull() }

        //whenThen
        topologyTestCase(invalidSample, assertMvpNotProduced)
    }

    private fun topologyTestCase(eventSample: String, assertion: MvpAssertion) {
        //given
        val invalidMvpEvent: MvpEvent = JsonConverter.readJsonFile(eventSample)
        val id: String = invalidMvpEvent.gameId

        //when
        val topologyTestDriver: TopologyTestDriver = initializeTestDriver()

        topologyTestDriver.use {
            val inputTopic: TestInputTopic<String, MvpEvent> = topologyTestDriver.createInputTopic("mvp", StringSerializer(), JsonSerializer())
            inputTopic.pipeInput(id, invalidMvpEvent)

            val store: KeyValueStore<String, NbaGameMvp> = topologyTestDriver.getKeyValueStore(MvpTopology.MVP_STORE)
            val nbaGameMvp: NbaGameMvp? = store[id]

            //then
            assertion.invoke(nbaGameMvp)
        }
    }

    private fun initializeTestDriver(): TopologyTestDriver {
        val streamsBuilder = StreamsBuilder()
        mvpTopology.buildPipeline(streamsBuilder)
        val topology: Topology =  streamsBuilder.build()
        return TopologyTestDriver(topology)
    }
}

typealias MvpAssertion = (NbaGameMvp?) -> Unit