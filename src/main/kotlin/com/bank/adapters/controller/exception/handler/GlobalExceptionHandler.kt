package com.bank.adapters.controller.exception.handler

import com.bank.domain.exception.PersonNotFoundException
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.server.exceptions.ExceptionHandler
import io.micronaut.http.server.exceptions.response.ErrorResponseProcessor
import jakarta.inject.Singleton

@Singleton
class GlobalExceptionHandler(
    private val errorResponseProcessor: ErrorResponseProcessor<Any>
) : ExceptionHandler<Exception, HttpResponse<*>> {

    override fun handle(request: HttpRequest<*>?, exception: Exception?): HttpResponse<*> {
        return when (exception) {
            is PersonNotFoundException -> HttpResponse.status<Any>(HttpStatus.NOT_FOUND, exception.message)
            else -> HttpResponse.status<Any>(HttpStatus.INTERNAL_SERVER_ERROR, exception?.message)
        }
    }
}