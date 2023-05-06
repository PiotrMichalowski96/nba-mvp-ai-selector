package pl.piter.commons.domain

import java.time.OffsetDateTime

data class MvpEvent(
    val gameId: String,
    val homeTeam: String,
    val awayTeam: String,
    val startTime: OffsetDateTime,
    val gameResult: GameResult,
    val mvp: Player,
    val commentAI: String,
)
