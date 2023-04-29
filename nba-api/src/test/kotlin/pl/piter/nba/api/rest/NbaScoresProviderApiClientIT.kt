package pl.piter.nba.api.rest

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import pl.piter.commons.api.model.scores.GameListResponse
import pl.piter.commons.api.model.scores.GameResponse
import pl.piter.commons.util.JsonConverter
import pl.piter.nba.api.config.FeignConfig

@SpringBootTest
@Import(FeignConfig::class)
class NbaScoresProviderApiClientIT(@Autowired val apiClient: NbaScoresProviderApiClient) {

    @Test
    fun `given game id when call external API get game by id then return expected game`() {
        //given
        val expectedGameSample = "src/test/resources/gameById.json"
        val expectedGame: GameResponse = JsonConverter.readJsonFile(expectedGameSample)
        val gameId = "2b07dbd1-ee88-411e-95f2-533755d65263"

        //when
        val actualGame: GameResponse = apiClient.findGame(gameId)

        //then
        assertThat(actualGame)
                .usingRecursiveComparison()
                .isEqualTo(expectedGame)
    }

    @Test
    fun `given date when call external API get games list by date then return expected games`() {
        //given
        val expectedGameListSample = "src/test/resources/gamesListByDate.json"
        val expectedGameList: GameListResponse = JsonConverter.readJsonFile(expectedGameListSample)

        val year = 2023
        val month = 4
        val day = 21

        //when
        val actualGameList: GameListResponse = apiClient.findGameList(year, month, day)

        //then
        assertThat(actualGameList)
                .usingRecursiveComparison()
                .isEqualTo(expectedGameList)
    }
}