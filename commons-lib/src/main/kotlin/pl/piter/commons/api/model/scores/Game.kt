package pl.piter.commons.api.model.scores

import java.time.OffsetDateTime

data class Game(
    val id: String,
    val scheduled: OffsetDateTime,
    val homePoints: Int,
    val awayPoints: Int,
    val home: TeamThick,
    val away: TeamThick,
)
