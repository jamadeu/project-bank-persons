package com.bank.adapters.controller

import com.bank.adapters.controller.dto.FindPersonByIdResponse
import com.bank.domain.service.PersonService
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get

@Controller("/persons")
class PersonsController(
    private val personService: PersonService
) {
    @Get("/{id}")
    fun findById(id: String): HttpResponse<Any> {
        val foundedPerson = personService.findById(id)
        return if (foundedPerson != null) {
            HttpResponse.ok(FindPersonByIdResponse.fromEntity(foundedPerson))
        } else {
            HttpResponse.notFound()
        }
    }
}