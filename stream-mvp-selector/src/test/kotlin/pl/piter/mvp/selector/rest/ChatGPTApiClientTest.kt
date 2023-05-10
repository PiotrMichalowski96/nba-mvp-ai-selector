package pl.piter.mvp.selector.rest

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import pl.piter.commons.api.model.chatgpt.ChatGPTRequest
import pl.piter.commons.api.model.chatgpt.ChatGPTResponse
import pl.piter.commons.util.JsonConverter
import pl.piter.mvp.selector.config.FeignConfig

@EnabledIfEnvironmentVariable(
    named = "chat-gpt-api_key",
    matches = "^(?=\\s*\\S).*$",
    disabledReason = "API Key must not be blank"
)
@SpringBootTest
@Import(FeignConfig::class)
class ChatGPTApiClientTest(@Autowired private val apiClient: ChatGPTApiClient) {

    @Test
    fun `given chat request when call chat GPT endpoint then return chat answer`() {
        //given
        val chatQuestionSample = "src/test/resources/requestChatGPT.json"
        val chatRequest: ChatGPTRequest = JsonConverter.readJsonFile(chatQuestionSample)

        //when
        val chatGPTResponse: ChatGPTResponse = apiClient.askChat(chatRequest)

        //then
        assertChatResponseIsNotBlank(chatGPTResponse)
    }

    private fun assertChatResponseIsNotBlank(chatGPTResponse: ChatGPTResponse) =
        assertThat(chatGPTResponse.choices[0].message.content).isNotBlank
}