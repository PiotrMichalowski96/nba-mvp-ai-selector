package pl.piter.nba.api.service

import feign.FeignException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
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
import pl.piter.nba.api.config.CacheConfig
import pl.piter.nba.api.exception.ScoreApiException
import pl.piter.nba.api.rest.NbaScoresProviderApiClient
import pl.piter.nba.api.util.RedisExtension
import java.time.LocalDate

@ExtendWith(SpringExtension::class, RedisExtension::class)
@Import(ScoreApiService::class, CacheConfig::class)
@ImportAutoConfiguration(CacheAutoConfiguration::class, RedisAutoConfiguration::class)
class ScoreApiServiceTest {

    @MockBean
    lateinit var apiClient: NbaScoresProviderApiClient

    @Autowired
    lateinit var scoreApiService: ScoreApiService

    @Autowired
    lateinit var cacheManager: CacheManager

    @Test
    fun `given game id when call get game endpoint then return game`() {
        //given
        val id = "987"
        val gameResponseSamplePath = "src/test/resources/gameById.json"
        val response: GameResponse = JsonConverter.readJsonFile(gameResponseSamplePath)

        mockGetGameEndpoint(id, response)

        //when
        val actualResponse: GameResponse = scoreApiService.callGame(id)

        //then
        assertThat(actualResponse).isEqualTo(response)
    }

    @Test
    fun `when error occurred during get game call then return exception`() {
        //given
        val id = "541"

        mockErrorGetGameEndpoint(id)

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

        mockGetGameListEndpoint(gameSchedule, response)

        //when
        val actualResponse: GameListResponse = scoreApiService.callGameList(gameSchedule)

        //then
        assertThat(actualResponse).isEqualTo(response)
    }

    @Test
    fun `given Redis caching when call get game by id then return game from cache`() {
        //given
        val id = "123"
        val gameResponseSamplePath = "src/test/resources/gameById.json"
        val response: GameResponse = JsonConverter.readJsonFile(gameResponseSamplePath)

        mockGetGameEndpoint(id, response)

        //when
        val responseCacheMiss: GameResponse = scoreApiService.callGame(id)
        val responseCacheHit: GameResponse = scoreApiService.callGame(id)


        //then
        assertThat(responseCacheMiss).isEqualTo(response)
        assertThat(responseCacheHit).isEqualTo(response)

        verify(apiClient, times(1)).findGame(id)
        assertThat(responseFromCache(id)).isEqualTo(response)
    }

    private fun mockGetGameEndpoint(id: String, response: GameResponse) =
        given(apiClient.findGame(id)).willReturn(response)

    private fun mockGetGameListEndpoint(gameSchedule: LocalDate, response: GameListResponse) =
        given(
            apiClient.findGameList(
                gameSchedule.year, gameSchedule.monthValue, gameSchedule.dayOfMonth
            )
        ).willReturn(response)

    private fun responseFromCache(id: String): Any? {
        return cacheManager.getCache("game")?.get(id)?.get()
    }

    private fun mockErrorGetGameEndpoint(id: String) {
        val notFoundException: FeignException = mock(FeignException::class.java)
        given(notFoundException.status()).willReturn(404)
        given(apiClient.findGame(id)).willThrow(notFoundException)
    }
}