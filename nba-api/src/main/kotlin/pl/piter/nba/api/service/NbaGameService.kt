package pl.piter.nba.api.service

import org.springframework.stereotype.Service
import pl.piter.commons.api.model.nba.NbaGame
import pl.piter.commons.api.model.scores.GameListResponse
import pl.piter.commons.api.model.scores.GameResponse
import pl.piter.nba.api.mapper.toNbaGame
import pl.piter.nba.api.mapper.toNbaGames
import java.time.LocalDate

@Service
class NbaGameService(private val scoreApiService: ScoreApiService) {

    fun getGameById(id: String): NbaGame {
        val gameResponse: GameResponse = scoreApiService.callGame(id)
        return gameResponse.toNbaGame()
    }

    fun getGameListByTime(gameTime: LocalDate): List<NbaGame> {
        val gameListResponse: GameListResponse = scoreApiService.callGameList(gameTime)
        return gameListResponse.toNbaGames()
    }
}