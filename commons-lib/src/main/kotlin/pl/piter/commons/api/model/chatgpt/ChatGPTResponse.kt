package pl.piter.commons.api.model.chatgpt

data class ChatGPTResponse(
    val id: String,
    val created: Long,
    val model: String,
    val choices: List<Choice>,
)
