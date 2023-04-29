package pl.piter.commons.api.model.scores

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.OffsetDateTime

data class Game(
    val id: String,
    val scheduled: OffsetDateTime,
    @JsonProperty("home_points")
    val homePoints: Int,
    @JsonProperty("away_points")
    val awayPoints: Int,
    val home: TeamThick,
    val away: TeamThick,
)
