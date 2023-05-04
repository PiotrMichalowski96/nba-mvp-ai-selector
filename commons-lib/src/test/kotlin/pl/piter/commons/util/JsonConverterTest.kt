package pl.piter.commons.util

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import pl.piter.commons.api.model.nba.NbaGame
import pl.piter.commons.api.model.scores.GameResponse

class JsonConverterTest {

    @Test
    fun `given file in resources when read file then return text from file`() {
        //given
        val filePath = "src/test/resources/match.json"

        //when
        val text: String = JsonConverter.readFileAsString(filePath)

        //then
        assertThat(text).isNotBlank
    }

    @Test
    fun `given file in resources when convert then return object`() {
        //given
        val filePath = "src/test/resources/match.json"

        //when
        val convertedObj: GameResponse = JsonConverter.readJsonFile(filePath)

        //then
        assertThat(convertedObj).isNotNull
    }

    @Test
    fun `given file in resources when convert json array then return list of object`() {
        //given
        val filePath = "src/test/resources/nbaGameList.json"

        //when
        val convertedList: List<NbaGame> = JsonConverter.readJsonArrayFile(filePath)

        //then
        assertThat(convertedList)
            .hasSize(3)
            .hasOnlyElementsOfType(NbaGame::class.java)
    }
}
