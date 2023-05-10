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
    @CsvSource(value = [
        "invalidAbsentAnswer.json,false"
    ])
    fun `given ChatGPT response when validate then return validation result`(
        sample: String,
        expectedResult: Boolean
    ) {
        //given
        val directory = "src/test/resources/validation/"
        val chatGPTResponse: ChatGPTResponse = JsonConverter.readJsonFile(directory + sample)

        //when
        val result: Boolean = responseValidator.validate(chatGPTResponse)

        //then
        assertThat(result).isEqualTo(expectedResult)
    }
}