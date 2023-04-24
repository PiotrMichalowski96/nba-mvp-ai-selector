package pl.piter.commons.api.model.nba

data class GameDTO(
    val id: String,
    val status: String,
    val scheduled: String,
    val timeZones: GameTimeZoneDTO,
    val home: TeamGameDTO,
    val away: TeamGameDTO,
)
