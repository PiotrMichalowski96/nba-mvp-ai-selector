package pl.piter.commons.api.model.chatgpt

import jakarta.validation.constraints.NotBlank

data class Message(
    val role: Role,
    @field:NotBlank
    val content: String,
)
