package pl.piter.nba.api.service

import feign.FeignException
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import pl.piter.commons.api.model.scores.GameListResponse
import pl.piter.commons.api.model.scores.GameResponse
import pl.piter.nba.api.exception.ScoreApiException
import pl.piter.nba.api.rest.NbaScoresProviderApiClient
import java.time.LocalDate

@Service
class ScoreApiService(private val apiClient: NbaScoresProviderApiClient) {

    companion object {
        @JvmStatic
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = LoggerFactory.getLogger(javaClass.enclosingClass)
    }

    @Cacheable(value = ["game"], key = "#id")
    fun callGame(id: String): GameResponse {
        try {
            logger.info("Call external API for nba game id: $id")
            return apiClient.findGame(id)
        } catch (e: FeignException) {
            logger.error("Error during API call game by id: $id")
            throw ScoreApiException.of(e)
        }
    }

    @Cacheable(value = ["gameList"], key = "#gameSchedule")
    fun callGameList(gameSchedule: LocalDate): GameListResponse {
        try {
            logger.info("Call external API for NBA game list by: $gameSchedule")
            return apiClient.findGameList(gameSchedule.year, gameSchedule.monthValue, gameSchedule.dayOfMonth)
        } catch (e: FeignException) {
            logger.error("Error during API call game list by: $gameSchedule")
            throw ScoreApiException.of(e)
        }
    }
}