package pl.piter.commons.api.model.scores

data class GameResponse(
    val id: String,
    val status: String,
    val scheduled: String,
    val timeZones: GameTimeZone,
    val home: Team,
    val away: Team,
)
