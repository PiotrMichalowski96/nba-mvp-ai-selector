package pl.piter.commons.api.model.scores

import java.time.OffsetDateTime

data class GameResponse(
    val id: String,
    val status: String,
    val scheduled: OffsetDateTime,
    val home: Team,
    val away: Team,
)
