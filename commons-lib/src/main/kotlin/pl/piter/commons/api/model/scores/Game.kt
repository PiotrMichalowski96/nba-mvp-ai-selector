package pl.piter.commons.api.model.scores

data class Game(
    val id: String,
    val scheduled: String,
    val homePoints: Int,
    val awayPoints: Int,
    val timeZones: GameTimeZone,
    val home: TeamThick,
    val away: TeamThick,
)
