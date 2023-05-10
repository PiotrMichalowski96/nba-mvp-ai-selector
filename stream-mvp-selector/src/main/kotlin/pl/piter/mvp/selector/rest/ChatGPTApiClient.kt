package pl.piter.mvp.selector.rest

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import pl.piter.commons.api.model.chatgpt.ChatGPTRequest
import pl.piter.commons.api.model.chatgpt.ChatGPTResponse
import pl.piter.mvp.selector.config.FeignConfig

@FeignClient(
    name = "chat-gpt-api-client",
    url = "\${chat-gpt.api.url}",
    configuration = [FeignConfig::class]
)
interface ChatGPTApiClient {

    @PostMapping("/chat/completions")
    fun askChat(@RequestBody request: ChatGPTRequest): ChatGPTResponse
}