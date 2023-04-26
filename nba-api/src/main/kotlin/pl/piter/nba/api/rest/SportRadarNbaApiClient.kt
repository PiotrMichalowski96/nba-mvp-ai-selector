package pl.piter.nba.api.rest

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import pl.piter.commons.api.model.scores.GameResponse
import pl.piter.nba.api.config.FeignConfig

@FeignClient(
    name = "nba-api-client",
    url = "#{sports-radar.api.url}",
    configuration = [FeignConfig::class]
)
interface SportRadarNbaApiClient {

    @GetMapping("/games/{id}/boxscore.json")
    fun findGame(@PathVariable id: String): GameResponse
}