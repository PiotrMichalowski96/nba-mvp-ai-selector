package pl.piter.commons.validation

import org.slf4j.LoggerFactory

fun interface Validatable<T> {

    companion object {
        @JvmStatic
        @Suppress("JAVA_CLASS_ON_COMPANION")
        val logger = LoggerFactory.getLogger(javaClass.enclosingClass)
    }

    fun validate(toValidate: T): Boolean

    fun logAndValidate(message: String, toValidate: T): Boolean {
        val valid: Boolean = validate(toValidate)
        if (!valid) {
            logger.warn(message)
        }
        return valid
    }
}