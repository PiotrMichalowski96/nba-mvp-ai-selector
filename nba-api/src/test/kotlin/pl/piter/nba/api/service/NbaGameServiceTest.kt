package pl.piter.nba.api.service

import com.ninjasquad.springmockk.MockkBean
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Import
import org.springframework.test.context.junit.jupiter.SpringExtension
import pl.piter.commons.api.model.nba.NbaGame
import pl.piter.commons.api.model.scores.GameListResponse
import pl.piter.commons.api.model.scores.GameResponse
import pl.piter.commons.util.JsonConverter
import pl.piter.commons.util.generateId
import pl.piter.nba.api.config.TestConfig
import pl.piter.nba.api.rest.NbaScoresProviderApiClient
import pl.piter.nba.api.util.MockedExternalAPI
import java.time.LocalDate

@ExtendWith(SpringExtension::class)
@Import(TestConfig::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class NbaGameServiceTest {

    @MockkBean
    private lateinit var apiClient: NbaScoresProviderApiClient

    @Autowired
    private lateinit var nbaGameService: NbaGameService

    private lateinit var mockedExternalAPI: MockedExternalAPI

    @BeforeAll
    fun init() {
        mockedExternalAPI = MockedExternalAPI(apiClient)
    }

    @Test
    fun `given id when get game by id then return game`() {
        //given
        val gameResponseSample = "src/test/resources/gameById.json"
        val gameResponse: GameResponse = JsonConverter.readJsonFile(gameResponseSample)

        val expectedNbaGameSample = "src/test/resources/nbaGame.json"
        val expectedNbaGame: NbaGame = JsonConverter.readJsonFile(expectedNbaGameSample)

        val id: String = generateId(10)
        mockedExternalAPI.mockGetGameEndpoint(id, gameResponse)

        //when
        val actualNbaGame: NbaGame = nbaGameService.getGameById(id)

        //then
        assertThat(actualNbaGame).isEqualTo(expectedNbaGame)
    }

    @Test
    fun `given game time when get game list by time then return game list`() {
        //given
        val gameListResponseSample = "src/test/resources/gamesListByDate.json"
        val gameListResponse: GameListResponse = JsonConverter.readJsonFile(gameListResponseSample)

        val expectedNbaGameListSample = "src/test/resources/nbaGameList.json"
        val expectedNbaGames: List<NbaGame> = JsonConverter.readJsonArrayFile(expectedNbaGameListSample)

        val gameTime: LocalDate = LocalDate.of(2023, 5, 4)
        mockedExternalAPI.mockGetGameListEndpoint(gameTime, gameListResponse)

        //when
        val actualNbaGames: List<NbaGame> = nbaGameService.getGameListByTime(gameTime)

        //then
        assertThat(actualNbaGames).isEqualTo(expectedNbaGames)
    }
}