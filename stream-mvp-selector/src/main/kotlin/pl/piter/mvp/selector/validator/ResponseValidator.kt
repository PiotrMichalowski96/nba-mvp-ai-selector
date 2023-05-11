package pl.piter.mvp.selector.validator

import pl.piter.commons.api.model.chatgpt.ChatGPTResponse
import pl.piter.commons.domain.Player
import pl.piter.commons.validation.Validatable

class ResponseValidator(
    private val validator: Validatable<Any>,
    private val players: Set<Player>
) : Validatable<ChatGPTResponse> {

    companion object {
        private const val PLAYER_SEMICOLON_REASON_PATTERN =
            "^\\s*[a-zA-Z]+(\\s+[a-zA-Z]+)*\\s*;\\s*\\S.*\\S\\s*$"
    }

    override fun validate(toValidate: ChatGPTResponse): Boolean =
        validationCriteria.all { it.validate(toValidate) }

    private val validationCriteria: Set<Validatable<ChatGPTResponse>> = setOf(
        Validatable { validator.validate(it) },
        Validatable { chatAnswersSize(it) },
        Validatable { answerCorrectFormat(it) },
        Validatable { containsMvpPlayer(it) },
        Validatable { containsReasonAI(it) }
    )

    private fun chatAnswersSize(response: ChatGPTResponse): Boolean = response.choices.size == 1

    private fun answerCorrectFormat(response: ChatGPTResponse): Boolean {
        val answer: String = fetchAnswerFrom(response)
        return PLAYER_SEMICOLON_REASON_PATTERN.toRegex().matches(answer)
    }

    private fun containsMvpPlayer(response: ChatGPTResponse): Boolean {
        val answer: String = fetchAnswerFrom(response)
        val player: String = answer.split(";")[0].trim()
        return players
            .map { it.name }
            .any { it == player }
    }

    private fun containsReasonAI(response: ChatGPTResponse): Boolean {
        val answer: String = fetchAnswerFrom(response)
        val reason: String = answer.split(";")[1].trim()
        return reason.isNotBlank()
    }

    private fun fetchAnswerFrom(response: ChatGPTResponse): String =
        response.choices[0].message.content
}

