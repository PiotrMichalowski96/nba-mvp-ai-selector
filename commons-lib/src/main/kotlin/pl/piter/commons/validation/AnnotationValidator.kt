package pl.piter.commons.validation

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.validation.BeanPropertyBindingResult
import org.springframework.validation.Errors
import org.springframework.validation.Validator

@Component
class AnnotationValidator(private val validator: Validator) : Validatable<Any> {

    companion object {
        @JvmStatic
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = LoggerFactory.getLogger(javaClass.enclosingClass)
    }

    override fun validate(toValidate: Any): Boolean {
        val errors: Errors = BeanPropertyBindingResult(toValidate, toValidate::class.simpleName ?: "any")
        validator.validate(toValidate, errors)
        if (errors.hasErrors()) {
            logger.info("Not valid object: ${errors.allErrors}")
            return false
        }
        return true
    }
}