package pl.piter.mvp.selector.mapper

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import pl.piter.commons.api.model.chatgpt.ChatGPTRequest
import pl.piter.commons.domain.NbaGameEvent
import pl.piter.commons.util.JsonConverter

class ChatGPTMapperTest {

    @Test
    fun `given nba game event when mapped then return ChatGPT request`() {
        //given
        val eventSample = "src/test/resources/nbaGameEvent.json"
        val nbaGameEvent: NbaGameEvent = JsonConverter.readJsonFile(eventSample)

        val expectedRequestSample = "src/test/resources/postChatGPT.json"
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
}