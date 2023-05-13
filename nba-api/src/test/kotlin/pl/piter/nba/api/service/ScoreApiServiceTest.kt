package pl.piter.nba.api.service

import com.ninjasquad.springmockk.MockkBean
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer
import org.springframework.cache.CacheManager
import org.springframework.context.annotation.Import
import org.springframework.test.context.ContextConfiguration
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
@ContextConfiguration(initializers = [ConfigDataApplicationContextInitializer::class])
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ScoreApiServiceTest {

    @MockkBean
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
        val gameResponseCacheMiss: GameResponse = scoreApiService.callGame(id)
        val gameResponseCacheHit: GameResponse = scoreApiService.callGame(id)


        //then
        assertThat(gameResponseCacheMiss).isEqualTo(response)
        assertThat(gameResponseCacheHit).isEqualTo(response)

        verify(exactly = 1) { apiClient.findGame(id) }
        assertThat(gameResponseFromCache(id)).isEqualTo(response)
    }

    @Test
    fun `given Redis caching when call get games list endpoint then return games list from cache`() {
        //given
        val gameSchedule: LocalDate = LocalDate.of(2023, 5, 12)
        val gamesResponseSamplePath = "src/test/resources/gamesListByDate.json"
        val response: GameListResponse = JsonConverter.readJsonFile(gamesResponseSamplePath)

        mockedExternalAPI.mockGetGameListEndpoint(gameSchedule, response)

        //when
        val gameListCacheMiss: GameListResponse = scoreApiService.callGameList(gameSchedule)
        val gameListCacheHit: GameListResponse = scoreApiService.callGameList(gameSchedule)

        //then
        assertThat(gameListCacheMiss).isEqualTo(response)
        assertThat(gameListCacheHit).isEqualTo(response)

        verify(exactly = 1) {
            apiClient.findGameList(
                gameSchedule.year,
                gameSchedule.monthValue,
                gameSchedule.dayOfMonth
            )
        }
        assertThat(gameListFromCache(gameSchedule)).isEqualTo(response)
    }

    private fun gameResponseFromCache(id: String): Any? =
        cacheManager.getCache("game")?.get(id)?.get()

    private fun gameListFromCache(gameSchedule: LocalDate): Any? =
        cacheManager.getCache("gameList")?.get(gameSchedule)?.get()
}