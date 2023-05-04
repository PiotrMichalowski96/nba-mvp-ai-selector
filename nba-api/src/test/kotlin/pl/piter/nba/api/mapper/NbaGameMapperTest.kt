package pl.piter.nba.api.mapper

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import pl.piter.commons.api.model.nba.NbaGame
import pl.piter.commons.api.model.scores.GameListResponse
import pl.piter.commons.api.model.scores.GameResponse
import pl.piter.commons.util.JsonConverter

class NbaGameMapperTest {

    @Test
    fun `given game response when mapped then return nba game`() {
        //given
        val gameResponseSample = "src/test/resources/gameById.json"
        val gameResponse: GameResponse = JsonConverter.readJsonFile(gameResponseSample)

        val expectedNbaGameSample = "src/test/resources/nbaGame.json"
        val expectedNbaGame: NbaGame = JsonConverter.readJsonFile(expectedNbaGameSample)

        //when
        val actualNbaGame: NbaGame = gameResponse.toNbaGame()

        //then
        assertThat(actualNbaGame).isEqualTo(expectedNbaGame)
    }

    @Test
    fun `given game list response when mapped then return list of nba games`() {
        //given
        val gameListResponseSample = "src/test/resources/gamesListByDate.json"
        val gameListResponse: GameListResponse = JsonConverter.readJsonFile(gameListResponseSample)

        val expectedNbaGameListSample = "src/test/resources/nbaGameList.json"
        val expectedNbaGames: List<NbaGame> = JsonConverter.readJsonArrayFile(expectedNbaGameListSample)

        //when
        val actualNbaGames: List<NbaGame> = gameListResponse.toNbaGames()

        //then
        assertThat(actualNbaGames).containsExactlyElementsOf(expectedNbaGames)
    }
}