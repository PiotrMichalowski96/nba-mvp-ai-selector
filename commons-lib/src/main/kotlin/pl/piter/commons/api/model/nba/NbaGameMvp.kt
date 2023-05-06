package pl.piter.commons.api.model.nba

import pl.piter.commons.domain.GameResult
import pl.piter.commons.domain.Player
import java.time.OffsetDateTime

data class NbaGameMvp(
    val homeTeam: String,
    val awayTeam: String,
    val startTime: OffsetDateTime,
    val gameResult: GameResult,
    val mvp: Player,
    val reason: String,
)
