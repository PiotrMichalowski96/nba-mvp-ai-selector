package pl.piter.mvp.selector.mapper

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import pl.piter.commons.api.model.chatgpt.ChatGPTRequest
import pl.piter.commons.api.model.chatgpt.ChatGPTResponse
import pl.piter.commons.domain.MvpEvent
import pl.piter.commons.domain.NbaGameEvent
import pl.piter.commons.util.JsonConverter

class ChatGPTMapperTest {

    @Test
    fun `given nba game event when mapped then return ChatGPT request`() {
        //given
        val eventSample = "src/test/resources/nbaGameEvent.json"
        val nbaGameEvent: NbaGameEvent = JsonConverter.readJsonFile(eventSample)

        val expectedRequestSample = "src/test/resources/requestChatGPT.json"
        val expectedChatGPTRequest: ChatGPTRequest = JsonConverter.readJsonFile(expectedRequestSample)

        //when
        val chatGPTRequest: ChatGPTRequest = nbaGameEvent.toChatGPTRequest()

        //then
        assertThat(chatGPTRequest)
            .usingRecursiveComparison()
            .ignoringFields("messages.content")
            .isEqualTo(expectedChatGPTRequest)

        assertThat(chatGPTRequest.messages[0].content)
            .hasSameSizeAs(expectedChatGPTRequest.messages[0].content)
    }

    @Test
    fun `given nba game event and ChatGPT response when mapped then return mvp event`() {
        //given
        val nbaGameEventSample = "src/test/resources/nbaGameEvent.json"
        val nbaGameEvent: NbaGameEvent = JsonConverter.readJsonFile(nbaGameEventSample)

        val chatResponseSample = "src/test/resources/responseChatGPT.json"
        val chatResponse: ChatGPTResponse = JsonConverter.readJsonFile(chatResponseSample)

        val expectedMvpSample = "src/test/resources/mvpEvent.json"
        val expectedMvpEvent: MvpEvent = JsonConverter.readJsonFile(expectedMvpSample)

        //when
        val actualMvpEvent: MvpEvent = nbaGameEvent.toMvpEvent(chatResponse)

        //then
        assertThat(actualMvpEvent).isEqualTo(expectedMvpEvent)
    }
}