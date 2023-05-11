package pl.piter.commons.api.model.chatgpt

import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank

data class ChatGPTResponse(
    @field:NotBlank
    val id: String,
    val created: Long,
    @field:NotBlank
    val model: String,
    @field:Valid
    val choices: List<Choice>,
)
