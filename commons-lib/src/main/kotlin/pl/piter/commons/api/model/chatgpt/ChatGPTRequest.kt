package pl.piter.commons.api.model.chatgpt

import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank

data class ChatGPTRequest(
    @field:NotBlank
    val model: String,
    @field:Valid
    val messages: List<Message>,
)
