package pl.piter.commons.api.model.nba

data class LeadersGameDTO(
    val points: List<PlayerGameDTO>,
    val rebounds: List<PlayerGameDTO>,
    val assists: List<PlayerGameDTO>,
)
