package pl.piter.commons.api.model.scores

data class GameListResponse(
    val date: String,
    val games: List<Game>,
)
