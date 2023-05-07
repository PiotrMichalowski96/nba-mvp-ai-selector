package pl.piter.nba.api.validation

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.validation.BeanPropertyBindingResult
import org.springframework.validation.Errors
import org.springframework.validation.Validator

@Component
class AnnotationValidator(private val validator: Validator) {

    companion object {
        @JvmStatic
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = LoggerFactory.getLogger(javaClass.enclosingClass)
    }

    fun validate(any: Any): Boolean {
        val errors: Errors = BeanPropertyBindingResult(any, any::class.simpleName ?: "any")
        validator.validate(any, errors)
        if (errors.hasErrors()) {
            logger.info("Not valid object: ${errors.allErrors}")
            return false
        }
        return true
    }
}