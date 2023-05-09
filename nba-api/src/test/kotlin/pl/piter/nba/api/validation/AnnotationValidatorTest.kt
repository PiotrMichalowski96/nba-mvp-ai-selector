package pl.piter.nba.api.validation

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
import pl.piter.commons.domain.MvpEvent
import pl.piter.commons.util.JsonConverter

@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [LocalValidatorFactoryBean::class])
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AnnotationValidatorTest() {

    @Autowired
    private lateinit var validator: Validator

    private lateinit var annotationValidator: AnnotationValidator

    @BeforeAll
    fun init() {
        annotationValidator = AnnotationValidator(validator)
    }

    @ParameterizedTest
    @CsvSource(value = [
        "src/test/resources/invalidMvpEvent.json, false",
        "src/test/resources/mvpEvent.json, true"])
    fun `should validate mvp object`(sample: String, expectedResult: Boolean) {
        //given
        val mvp: MvpEvent = JsonConverter.readJsonFile(sample)

        //when
        val result: Boolean = annotationValidator.validate(mvp)

        //then
        assertThat(result).isEqualTo(expectedResult)
    }
}