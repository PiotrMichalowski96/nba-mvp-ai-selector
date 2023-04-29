package pl.piter.nba.api.rest

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import pl.piter.commons.api.model.scores.GameListResponse
import pl.piter.commons.api.model.scores.GameResponse
import pl.piter.nba.api.config.FeignConfig

@FeignClient(
    name = "nba-api-client",
    url = "\${nba-scores-provider.api.url}",
    configuration = [FeignConfig::class]
)
interface NbaScoresProviderApiClient {

    @GetMapping("/games/{id}/boxscore.json")
    fun findGame(@PathVariable id: String): GameResponse

    @GetMapping("/games/{year}/{month}/{day}/schedule.json")
    fun findGameList(@PathVariable year: Int,
                     @PathVariable month: Int,
                     @PathVariable day: Int): GameListResponse
}