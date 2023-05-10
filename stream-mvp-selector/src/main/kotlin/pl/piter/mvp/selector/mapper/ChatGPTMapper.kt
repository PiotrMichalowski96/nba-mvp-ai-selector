package pl.piter.mvp.selector.mapper

import pl.piter.commons.api.model.chatgpt.ChatGPTRequest
import pl.piter.commons.api.model.chatgpt.ChatGPTResponse
import pl.piter.commons.api.model.chatgpt.Message
import pl.piter.commons.api.model.chatgpt.Role
import pl.piter.commons.domain.MvpEvent
import pl.piter.commons.domain.NbaGameEvent
import pl.piter.commons.domain.Player

fun NbaGameEvent.toChatGPTRequest() = ChatGPTRequest(
    model = "gpt-3.5-turbo",
    messages = listOf(
        Message(Role.user, createChatQuestion(this))
    )
)

private fun createChatQuestion(nbaGameEvent: NbaGameEvent): String =
    "Please select mvp of NBA game: ${nbaGameEvent.awayTeam} - ${nbaGameEvent.homeTeam}, " +
            "result ${nbaGameEvent.gameResult.awayTeamPoints} - ${nbaGameEvent.gameResult.homeTeamPoints}. " +
            "Players statistics: ${describeStats(nbaGameEvent.bestPlayers)} " +
            "Please respond in format only: playername;reason"

private fun describeStats(bestPlayers: Set<Player>): String = bestPlayers
    .mapIndexed { index, player -> describePlayerStats(index, player) }
    .joinToString(separator = "\n", postfix = ".\n")

private fun describePlayerStats(index: Int, player: Player): String =
    "${index + 1}) ${player.name}, ${player.team} team, ${player.stats}"


fun NbaGameEvent.toMvpEvent(chatGPTResponse: ChatGPTResponse) = MvpEvent(
    gameId = gameId,
    homeTeam = homeTeam,
    awayTeam = awayTeam,
    startTime = startTime,
    gameResult = gameResult,
    mvp = selectMvp(chatGPTResponse),
    commentAI = selectReason(chatGPTResponse)
)

private fun NbaGameEvent.selectMvp(response: ChatGPTResponse): Player {
    val answerFields: List<String> = fetchChatGPTAnswers(response)
    val playerName: String = answerFields[0]
    return bestPlayers.first { it.name == playerName }
}

private fun fetchChatGPTAnswers(response: ChatGPTResponse): List<String> {
    val chatAnswer: String = response.choices[0].message.content
    val answerFields: List<String> = chatAnswer.split(";")
    require(answerFields.size == 2)
    return answerFields
}

private fun selectReason(chatGPTResponse: ChatGPTResponse): String =
    fetchChatGPTAnswers(chatGPTResponse)[1].trim()
