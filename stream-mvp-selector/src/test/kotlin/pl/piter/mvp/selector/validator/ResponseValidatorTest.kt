package pl.piter.mvp.selector.validator

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.validation.Validator
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean
import pl.piter.commons.api.model.chatgpt.ChatGPTResponse
import pl.piter.commons.api.model.chatgpt.Choice
import pl.piter.commons.api.model.chatgpt.Message
import pl.piter.commons.api.model.chatgpt.Role
import pl.piter.commons.domain.Player
import pl.piter.commons.util.JsonConverter
import pl.piter.commons.validation.AnnotationValidator

@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [LocalValidatorFactoryBean::class])
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ResponseValidatorTest {

    @Autowired
    private lateinit var validator: Validator

    private lateinit var responseValidator: ResponseValidator

    @BeforeAll
    fun init() {
        val playersSample = "src/test/resources/players.json"
        val players: Set<Player> = JsonConverter.readJsonArrayFile<Player>(playersSample).toSet()
        val annotationValidator = AnnotationValidator(validator)
        responseValidator = ResponseValidator(annotationValidator, players)
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "invalidAbsentAnswer.json, false",
            "invalidAIComment.json, false",
            "invalidFormat.json, false",
            "invalidId.json, false",
            "invalidPlayerName.json, false",
            "valid.json, true"
        ]
    )
    fun `given ChatGPT response when validate then return validation result`(
        sample: String,
        expectedResult: Boolean
    ) {
        //given
        val directory = "src/test/resources/validation/"
        val chatGPTResponse: ChatGPTResponse = JsonConverter.readJsonFile(directory + sample)

        //when
        val result: Boolean = responseValidator.logAndValidate("Chat response is not valid",
            chatGPTResponse)

        //then
        assertThat(result).isEqualTo(expectedResult)
    }

    @ParameterizedTest
    @CsvSource(
        delimiter = '#', ignoreLeadingAndTrailingWhitespace = true, value = [
            "Trae Young; impressive performance with high points, assists and blocks.# true",
            "Trae Young, basketball# false",
            " Trae Young ; reason # true",
            "Trae Young ; comment# true",
            " Trae Young;opinion# true",
            "jayson tatum;reason# true",
            " ;some-words# false",
            "some-words; # false",
            " ; # false"
        ]
    )
    fun `given answer format when validate against regex then return validation result`(
        answer: String,
        expectedResult: Boolean
    ) {
        //given
        val chatGPTResponse: ChatGPTResponse = createResponse(answer)

        //when
        val result: Boolean = responseValidator.validate(chatGPTResponse)

        //then
        assertThat(result).isEqualTo(expectedResult)
    }

    private fun createResponse(answer: String): ChatGPTResponse {
        val message = Message(Role.user, answer)
        val choice = Choice(message, "reason", 123)
        return ChatGPTResponse(
            "123",
            123L,
            "chat-gpt4",
            listOf(choice)
        )
    }
}