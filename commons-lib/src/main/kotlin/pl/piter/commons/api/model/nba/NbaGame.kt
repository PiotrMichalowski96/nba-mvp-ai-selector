package pl.piter.commons.api.model.nba

import org.springframework.hateoas.RepresentationModel
import pl.piter.commons.domain.GameResult
import java.time.OffsetDateTime

data class NbaGame(
    val id: String,
    val homeTeam: String,
    val awayTeam: String,
    val startTime: OffsetDateTime,
    val gameResult: GameResult,
) : RepresentationModel<NbaGame>()
