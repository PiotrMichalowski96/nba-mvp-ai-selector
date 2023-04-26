package pl.piter.nba.api.rest

import org.springframework.cache.annotation.Cacheable
import pl.piter.commons.api.model.scores.GameResponse

class CachingApiClientService (private val service: ApiClientService): ApiClientService by service {

    @Cacheable(value = ["game"], key = "#id")
    override fun getGame(id: String): GameResponse? {
        return service.getGame(id)
    }
}