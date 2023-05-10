package pl.piter.commons.api.model.chatgpt

import com.fasterxml.jackson.annotation.JsonProperty

data class Choice(
    val message: Message,
    @JsonProperty("finish_reason")
    val finishReason: String,
    val index: Int,
)
