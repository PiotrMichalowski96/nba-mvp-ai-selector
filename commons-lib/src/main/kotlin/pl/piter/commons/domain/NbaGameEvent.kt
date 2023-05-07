package pl.piter.commons.domain

import jakarta.validation.constraints.NotBlank
import java.time.OffsetDateTime

data class NbaGameEvent(
    @field:NotBlank val gameId: String,
    @field:NotBlank val homeTeam: String,
    @field:NotBlank val awayTeam: String,
    val startTime: OffsetDateTime,
    val gameResult: GameResult,
    val bestPlayers: List<Player>,
)
