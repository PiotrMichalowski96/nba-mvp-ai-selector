package pl.piter.commons.validation

fun interface Validatable<T> {

    fun validate(toValidate: T): Boolean
}