package pl.piter.commons.domain

data class GameResult(
    val homeTeam: String,
    val awayTeam: String,
    val homeTeamPoints: Int,
    val awayTeamPoints: Int,
)
