package pl.piter.nba.api.rest

import pl.piter.commons.api.model.scores.GameResponse

interface ApiClientService {

    fun getGame(id: String): GameResponse?
}