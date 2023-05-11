package pl.piter.mvp.selector.service

import com.ninjasquad.springmockk.MockkBean
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Import
import org.springframework.test.context.junit.jupiter.SpringExtension
import pl.piter.commons.api.model.chatgpt.ChatGPTRequest
import pl.piter.commons.api.model.chatgpt.ChatGPTResponse
import pl.piter.commons.util.JsonConverter
import pl.piter.mvp.selector.exception.ChatGPTException
import pl.piter.mvp.selector.rest.ChatGPTApiClient
import pl.piter.mvp.selector.util.MockedChatGPT

@ExtendWith(SpringExtension::class)
@Import(ChatGPTApiService::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ChatGPTApiServiceTest {

    @MockkBean
    private lateinit var apiClient: ChatGPTApiClient

    @Autowired
    private lateinit var chatGPTApiService: ChatGPTApiService

    private lateinit var mockedChatGPT: MockedChatGPT

    @BeforeAll
    fun setup() {
        mockedChatGPT = MockedChatGPT(apiClient)
    }

    @Test
    fun `given request when call to ChatGPT API then return response`() {
        //given
        val requestSample = "src/test/resources/requestChatGPT.json"
        val request: ChatGPTRequest = JsonConverter.readJsonFile(requestSample)

        val responseSample = "src/test/resources/responseChatGPT.json"
        val response: ChatGPTResponse = JsonConverter.readJsonFile(responseSample)

        mockedChatGPT.mockAskChatGPT(request, response)

        //when
        val actualResponse: ChatGPTResponse = chatGPTApiService.callChat(request)

        //then
        assertThat(actualResponse).isEqualTo(response)
    }

    @Test
    fun `given request when error during call to ChatGPT API then throws exception`() {
        //given
        val requestSample = "src/test/resources/requestChatGPT.json"
        val request: ChatGPTRequest = JsonConverter.readJsonFile(requestSample)

        mockedChatGPT.mockErrorCallChatGPT(request)

        //whenThen
        assertThatThrownBy { chatGPTApiService.callChat(request) }
            .isInstanceOf(ChatGPTException::class.java)
    }
}