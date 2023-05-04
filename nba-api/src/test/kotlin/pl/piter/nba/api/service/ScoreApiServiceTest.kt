package pl.piter.nba.api.service

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.cache.CacheManager
import org.springframework.context.annotation.Import
import org.springframework.test.context.junit.jupiter.SpringExtension
import pl.piter.commons.api.model.scores.GameListResponse
import pl.piter.commons.api.model.scores.GameResponse
import pl.piter.commons.util.JsonConverter
import pl.piter.commons.util.generateId
import pl.piter.nba.api.config.CacheConfig
import pl.piter.nba.api.exception.ScoreApiException
import pl.piter.nba.api.rest.NbaScoresProviderApiClient
import pl.piter.nba.api.util.MockedExternalAPI
import pl.piter.nba.api.util.RedisExtension
import java.time.LocalDate

@ExtendWith(SpringExtension::class, RedisExtension::class)
@Import(ScoreApiService::class, CacheConfig::class)
@ImportAutoConfiguration(CacheAutoConfiguration::class, RedisAutoConfiguration::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ScoreApiServiceTest {

    @MockBean
    private lateinit var apiClient: NbaScoresProviderApiClient

    @Autowired
    private lateinit var scoreApiService: ScoreApiService

    @Autowired
    private lateinit var cacheManager: CacheManager

    private lateinit var mockedExternalAPI: MockedExternalAPI

    @BeforeAll
    fun setup() {
        mockedExternalAPI = MockedExternalAPI(apiClient)
    }

    @Test
    fun `given game id when call get game endpoint then return game`() {
        //given
        val id: String = generateId(10)
        val gameResponseSamplePath = "src/test/resources/gameById.json"
        val response: GameResponse = JsonConverter.readJsonFile(gameResponseSamplePath)

        mockedExternalAPI.mockGetGameEndpoint(id, response)

        //when
        val actualResponse: GameResponse = scoreApiService.callGame(id)

        //then
        assertThat(actualResponse).isEqualTo(response)
    }

    @Test
    fun `when error occurred during get game call then return exception`() {
        //given
        val id: String = generateId(10)

        mockedExternalAPI.mockErrorGetGameEndpoint(id)

        //whenThen
        assertThatThrownBy {
            scoreApiService.callGame(id)
        }.isInstanceOf(ScoreApiException::class.java)
    }

    @Test
    fun `given game schedule when call get games list endpoint then return game list`() {
        //given
        val gameSchedule: LocalDate = LocalDate.of(2023, 4, 30)
        val gamesResponseSamplePath = "src/test/resources/gamesListByDate.json"
        val response: GameListResponse = JsonConverter.readJsonFile(gamesResponseSamplePath)

        mockedExternalAPI.mockGetGameListEndpoint(gameSchedule, response)

        //when
        val actualResponse: GameListResponse = scoreApiService.callGameList(gameSchedule)

        //then
        assertThat(actualResponse).isEqualTo(response)
    }

    @Test
    fun `given Redis caching when call get game by id then return game from cache`() {
        //given
        val id: String = generateId(10)
        val gameResponseSamplePath = "src/test/resources/gameById.json"
        val response: GameResponse = JsonConverter.readJsonFile(gameResponseSamplePath)

        mockedExternalAPI.mockGetGameEndpoint(id, response)

        //when
        val responseCacheMiss: GameResponse = scoreApiService.callGame(id)
        val responseCacheHit: GameResponse = scoreApiService.callGame(id)


        //then
        assertThat(responseCacheMiss).isEqualTo(response)
        assertThat(responseCacheHit).isEqualTo(response)

        verify(apiClient, times(1)).findGame(id)
        assertThat(responseFromCache(id)).isEqualTo(response)
    }

    private fun responseFromCache(id: String): Any? {
        return cacheManager.getCache("game")?.get(id)?.get()
    }
}