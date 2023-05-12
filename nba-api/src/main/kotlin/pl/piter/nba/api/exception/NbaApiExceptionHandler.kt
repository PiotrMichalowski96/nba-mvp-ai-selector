package pl.piter.nba.api.exception

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@ControllerAdvice
class NbaApiExceptionHandler : ResponseEntityExceptionHandler() {

    @ExceptionHandler(value = [ScoreApiException::class])
    fun handle(ex: RuntimeException, request: WebRequest): ResponseEntity<Any>? {
        val responseBody = "Cannot get response from NBA games scores provider"
        return handleExceptionInternal(
            ex,
            responseBody,
            HttpHeaders(),
            HttpStatus.BAD_REQUEST,
            request
        )
    }
}