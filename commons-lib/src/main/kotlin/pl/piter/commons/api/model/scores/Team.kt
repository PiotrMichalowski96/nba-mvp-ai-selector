package pl.piter.commons.api.model.scores

data class Team(
    val name: String,
    val market: String,
    val points: Int,
    val leaders: Leaders,
)