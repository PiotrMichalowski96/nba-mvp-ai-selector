package pl.piter.commons.api.model.chatgpt

data class Choice(
    val message: Message,
    val finishReason: String,
    val index: Int,
)
