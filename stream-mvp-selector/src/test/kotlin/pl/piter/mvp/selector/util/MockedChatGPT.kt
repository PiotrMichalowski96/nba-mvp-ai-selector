package pl.piter.mvp.selector.util

import feign.FeignException
import io.mockk.every
import io.mockk.mockk
import pl.piter.commons.api.model.chatgpt.ChatGPTRequest
import pl.piter.commons.api.model.chatgpt.ChatGPTResponse
import pl.piter.mvp.selector.rest.ChatGPTApiClient

class MockedChatGPT(private val apiClient: ChatGPTApiClient) {

    fun mockAskChatGPT(request: ChatGPTRequest, response: ChatGPTResponse) =
        every { apiClient.askChat(request) } returns response

    fun mockErrorCallChatGPT(request: ChatGPTRequest) {
        val error: FeignException = mockk<FeignException>(relaxed = true)
        every { apiClient.askChat(request) } throws error
    }
}