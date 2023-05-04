package pl.piter.nba.api.util

import feign.FeignException
import io.mockk.every
import io.mockk.mockk
import pl.piter.commons.api.model.scores.GameListResponse
import pl.piter.commons.api.model.scores.GameResponse
import pl.piter.nba.api.rest.NbaScoresProviderApiClient
import java.time.LocalDate

class MockedExternalAPI(private val apiClient: NbaScoresProviderApiClient) {

    fun mockGetGameEndpoint(id: String, response: GameResponse) =
        every { apiClient.findGame(id) } returns response

    fun mockGetGameListEndpoint(gameSchedule: LocalDate, response: GameListResponse) =
        every {
            apiClient.findGameList(
                gameSchedule.year,
                gameSchedule.monthValue,
                gameSchedule.dayOfMonth
            )
        } returns response

    fun mockErrorGetGameEndpoint(id: String) {
        val notFoundException: FeignException = mockk<FeignException>(relaxed = true)
        every { apiClient.findGame(id) } throws notFoundException
    }
}