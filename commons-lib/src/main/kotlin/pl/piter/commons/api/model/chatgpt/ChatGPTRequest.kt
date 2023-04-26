package pl.piter.commons.api.model.chatgpt

data class ChatGPTRequest(
    val model: String,
    val messages: List<Message>,
)
