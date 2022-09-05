package com.bank.adapters.controller.exception.handler

import com.bank.domain.exception.InvalidCpfException
import com.bank.domain.exception.PersonAlreadyExistsException
import com.bank.domain.exception.PersonNotFoundException
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.server.exceptions.ExceptionHandler
import io.micronaut.http.server.exceptions.response.ErrorResponseProcessor
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory

@Singleton
class GlobalExceptionHandler(
    private val errorResponseProcessor: ErrorResponseProcessor<Any>
) : ExceptionHandler<Exception, HttpResponse<*>> {

    private val logger = LoggerFactory.getLogger(javaClass)
    override fun handle(request: HttpRequest<*>?, exception: Exception?): HttpResponse<*> {
        logger.error("GlobalExceptionHandler, request - $request, excepton - $exception")
        return when (exception) {
            is PersonNotFoundException -> HttpResponse.notFound(ErrorBody.errorBody(exception.message, 404))
            is PersonAlreadyExistsException -> HttpResponse.badRequest(ErrorBody.errorBody(exception.message, 400))
            is InvalidCpfException -> HttpResponse.badRequest(ErrorBody.errorBody(exception.message, 400))
            else -> HttpResponse.serverError(ErrorBody.errorBody(exception?.message, 500))
        }
    }
}

class ErrorBody(
    val message: String,
    val statusCode: Int
) {
    companion object {
        fun errorBody(message: String?, statusCode: Int): ErrorBody =
            ErrorBody(
                message ?: "Unexpected error",
                statusCode
            )
    }
}