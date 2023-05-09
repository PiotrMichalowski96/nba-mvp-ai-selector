package pl.piter.nba.api.mapper

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import pl.piter.commons.api.model.nba.NbaGameMvp
import pl.piter.commons.domain.MvpEvent
import pl.piter.commons.util.JsonConverter

class MvpMapperTest {

    @Test
    fun `given mvp event when mapping then return nba game mvp api model`() {
        //given
        val mvpEventSample = "src/test/resources/mvpEvent.json"
        val mvpEvent: MvpEvent = JsonConverter.readJsonFile(mvpEventSample)

        val expectedNbaGameMvpSample = "src/test/resources/nbaGameMvp.json"
        val expectedNbaGameMvp: NbaGameMvp = JsonConverter.readJsonFile(expectedNbaGameMvpSample)

        //when
        val actualNbaGameMvp: NbaGameMvp = mvpEvent.toNbaGameMvp()

        //then
        assertThat(actualNbaGameMvp).isEqualTo(expectedNbaGameMvp)
    }
}