package pl.piter.commons.domain

import java.time.OffsetDateTime

data class NbaGameEvent(
    val gameId: String,
    val homeTeam: String,
    val awayTeam: String,
    val startTime: OffsetDateTime,
    val gameResult: GameResult,
    val bestPlayers: List<Player>,
)
