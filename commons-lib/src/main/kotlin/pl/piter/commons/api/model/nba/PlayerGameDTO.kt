package pl.piter.commons.api.model.nba

data class PlayerGameDTO(
    val id: String,
    val fullName: String,
    val statistics: StatisticsDTO,
)
