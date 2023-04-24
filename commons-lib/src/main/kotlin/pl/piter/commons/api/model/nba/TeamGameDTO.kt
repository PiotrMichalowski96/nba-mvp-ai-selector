package pl.piter.commons.api.model.nba

data class TeamGameDTO(
    val name: String,
    val market: String,
    val points: Int,
    val leaders: LeadersGameDTO,
)