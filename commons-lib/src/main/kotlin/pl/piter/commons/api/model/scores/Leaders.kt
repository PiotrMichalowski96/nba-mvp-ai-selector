package pl.piter.commons.api.model.scores

data class Leaders(
    val points: List<PlayerGame>,
    val rebounds: List<PlayerGame>,
    val assists: List<PlayerGame>,
)
