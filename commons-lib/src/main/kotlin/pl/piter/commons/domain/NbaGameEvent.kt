package pl.piter.commons.domain

import java.time.ZonedDateTime

data class NbaGameEvent(
    val id: String,
    val homeTeam: String,
    val awayTeam: String,
    val startTime: ZonedDateTime,
    val gameResult: GameResult,
    val bestPlayers: List<Player>,
)
