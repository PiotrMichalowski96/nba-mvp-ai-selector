package pl.piter.nba.api.rest

import pl.piter.commons.api.model.scores.GameResponse

class DefaultApiClientService(private val apiClient: SportRadarNbaApiClient): ApiClientService {

    override fun getGame(id: String): GameResponse? {
        return apiClient.findGame(id)
    }
}