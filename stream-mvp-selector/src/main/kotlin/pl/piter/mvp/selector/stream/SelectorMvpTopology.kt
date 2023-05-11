package pl.piter.mvp.selector.stream

import org.apache.kafka.common.serialization.Serde
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.Consumed
import org.apache.kafka.streams.kstream.KStream
import org.apache.kafka.streams.kstream.Produced
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.support.serializer.JsonSerde
import org.springframework.stereotype.Component
import pl.piter.commons.api.model.chatgpt.ChatGPTRequest
import pl.piter.commons.api.model.chatgpt.ChatGPTResponse
import pl.piter.commons.domain.MvpEvent
import pl.piter.commons.domain.NbaGameEvent
import pl.piter.commons.domain.Player
import pl.piter.commons.validation.AnnotationValidator
import pl.piter.mvp.selector.config.TopicProperties
import pl.piter.mvp.selector.mapper.toChatGPTRequest
import pl.piter.mvp.selector.mapper.toMvpEvent
import pl.piter.mvp.selector.service.ChatGPTApiService
import pl.piter.mvp.selector.validator.ResponseValidator

@Component
class SelectorMvpTopology(
    private val topicProperties: TopicProperties,
    private val validator: AnnotationValidator,
    private val chatGPTApiService: ChatGPTApiService
) {

    companion object {
        @JvmStatic
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = LoggerFactory.getLogger(javaClass.enclosingClass)
    }

    @Autowired
    fun buildPipeline(streamsBuilder: StreamsBuilder) {
        val mvpSerde: Serde<MvpEvent> = JsonSerde(MvpEvent::class.java)

        nbaGameEvents(streamsBuilder)
            .peek { gameId, _ -> logger.info("Start processing event [key=$gameId]")}
            .filter { _, gameEvent -> validator.validate(gameEvent) }
            .mapValues { gameEvent -> callChatGPT(gameEvent) }
            .filter { _, eventContainer -> validateChatResponse(eventContainer) }
            .mapValues { eventContainer -> eventContainer.nbaGameEvent.toMvpEvent(eventContainer.chatGPTResponse) }
            .peek { gameId, _ -> logger.info("Sending event to MVP topic [gameId=$gameId]")}
            .to(topicProperties.mvp, Produced.with(Serdes.String(), mvpSerde))
    }

    private fun nbaGameEvents(streamsBuilder: StreamsBuilder): KStream<String, NbaGameEvent> {
        val nbaGameSerde: Serde<NbaGameEvent> = JsonSerde(NbaGameEvent::class.java)
        val nbaGameConsumed = Consumed.with(Serdes.String(), nbaGameSerde)
        return streamsBuilder.stream(topicProperties.match, nbaGameConsumed)
    }

    private fun callChatGPT(nbaGameEvent: NbaGameEvent): EventContainer {
        val request: ChatGPTRequest = nbaGameEvent.toChatGPTRequest()
        val response: ChatGPTResponse = chatGPTApiService.callChat(request)
        return EventContainer(nbaGameEvent, response)
    }

    private fun validateChatResponse(eventContainer: EventContainer): Boolean {
        val players: Set<Player> = eventContainer.nbaGameEvent.bestPlayers
        val responseValidator = ResponseValidator(validator, players)
        val validationResult: Boolean = responseValidator.validate(eventContainer.chatGPTResponse)
        if (!validationResult) {
            logger.error("Chat response is not valid!")
        }
        return validationResult
    }
}