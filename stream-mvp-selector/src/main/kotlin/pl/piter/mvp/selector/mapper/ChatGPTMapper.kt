package pl.piter.mvp.selector.mapper

import pl.piter.commons.api.model.chatgpt.ChatGPTRequest
import pl.piter.commons.api.model.chatgpt.Message
import pl.piter.commons.api.model.chatgpt.Role
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
    "${index+1}) ${player.name}, ${player.team} team, ${player.stats}"
