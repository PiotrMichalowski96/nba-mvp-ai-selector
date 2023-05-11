package pl.piter.commons.api.model.chatgpt

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank

data class Choice(
    @field:Valid
    val message: Message,
    @field:NotBlank
    @JsonProperty("finish_reason")
    val finishReason: String,
    val index: Int,
)
