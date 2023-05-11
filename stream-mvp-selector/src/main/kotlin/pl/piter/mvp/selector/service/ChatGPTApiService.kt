package pl.piter.mvp.selector.service

import feign.FeignException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import pl.piter.commons.api.model.chatgpt.ChatGPTRequest
import pl.piter.commons.api.model.chatgpt.ChatGPTResponse
import pl.piter.mvp.selector.exception.ChatGPTException
import pl.piter.mvp.selector.rest.ChatGPTApiClient

@Service
class ChatGPTApiService(private val apiClient: ChatGPTApiClient) {

    companion object {
        @JvmStatic
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = LoggerFactory.getLogger(javaClass.enclosingClass)
    }

    fun callChat(request: ChatGPTRequest): ChatGPTResponse {
        try {
            logger.info("Call ChatGPT API for request: $request")
            return apiClient.askChat(request)
        } catch (e: FeignException) {
            logger.error("Error during ChatGPT API call")
            throw ChatGPTException.of(e)
        }
    }
}