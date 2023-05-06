package pl.piter.nba.api.controller

import com.ninjasquad.springmockk.MockkBean
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import pl.piter.commons.api.model.scores.GameListResponse
import pl.piter.commons.api.model.scores.GameResponse
import pl.piter.commons.util.JsonConverter
import pl.piter.commons.util.generateId
import pl.piter.nba.api.config.TestConfig
import pl.piter.nba.api.rest.NbaScoresProviderApiClient
import pl.piter.nba.api.util.MockedExternalAPI
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@WebMvcTest(NbaGameController::class)
@Import(TestConfig::class)
class NbaGameControllerTest(@Autowired private val mockMvc: MockMvc) {

    @MockkBean
    private lateinit var apiClient: NbaScoresProviderApiClient

    private lateinit var mockedExternalAPI: MockedExternalAPI

    @BeforeEach
    fun init() {
        mockedExternalAPI = MockedExternalAPI(apiClient)
    }

    @Test
    fun `given id when get game endpoint call then return game`() {
        //given
        val gameResponseSample = "src/test/resources/gameById.json"
        val gameResponse: GameResponse = JsonConverter.readJsonFile(gameResponseSample)

        val id: String = generateId(10)
        mockedExternalAPI.mockGetGameEndpoint(id, gameResponse)

        //whenThen
        mockMvc.perform(get("/nba-game/$id"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.homeTeam").value("Atlanta Hawks"))
    }

    @Test
    fun `given game time when get game list endpoint call then return games`() {
        //given
        val gameListResponseSample = "src/test/resources/gamesListByDate.json"
        val gameListResponse: GameListResponse = JsonConverter.readJsonFile(gameListResponseSample)

        val gameTime: LocalDate = LocalDate.of(2023, 5, 4)
        mockedExternalAPI.mockGetGameListEndpoint(gameTime, gameListResponse)

        //whenThen
        mockMvc.perform(get("/nba-game")
            .param("game-time", gameTime.format(DateTimeFormatter.ISO_DATE)))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.length()").value(3))
    }
}