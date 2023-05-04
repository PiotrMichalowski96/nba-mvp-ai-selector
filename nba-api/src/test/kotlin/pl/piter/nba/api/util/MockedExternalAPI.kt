package pl.piter.nba.api.util

import feign.FeignException
import org.mockito.BDDMockito
import org.mockito.Mockito
import pl.piter.commons.api.model.scores.GameListResponse
import pl.piter.commons.api.model.scores.GameResponse
import pl.piter.nba.api.rest.NbaScoresProviderApiClient
import java.time.LocalDate

class MockedExternalAPI(private val apiClient: NbaScoresProviderApiClient) {

    fun mockGetGameEndpoint(id: String, response: GameResponse) =
        BDDMockito.given(apiClient.findGame(id)).willReturn(response)

    fun mockGetGameListEndpoint(gameSchedule: LocalDate, response: GameListResponse) =
        BDDMockito.given(
            apiClient.findGameList(
                gameSchedule.year, gameSchedule.monthValue, gameSchedule.dayOfMonth
            )
        ).willReturn(response)

    fun mockErrorGetGameEndpoint(id: String) {
        val notFoundException: FeignException = Mockito.mock(FeignException::class.java)
        BDDMockito.given(notFoundException.status()).willReturn(404)
        BDDMockito.given(apiClient.findGame(id)).willThrow(notFoundException)
    }
}