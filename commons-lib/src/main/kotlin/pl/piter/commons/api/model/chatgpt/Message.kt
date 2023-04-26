package pl.piter.commons.api.model.chatgpt

data class Message(
    val role: Role,
    val content: String,
)
