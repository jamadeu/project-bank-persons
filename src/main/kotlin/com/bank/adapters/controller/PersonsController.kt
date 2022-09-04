package com.bank.adapters.controller

import com.bank.adapters.controller.dto.FindPersonByIdResponse
import com.bank.domain.model.Person
import com.bank.adapters.repository.MicronautDataRepository
import com.bank.domain.service.PersonService
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import org.bson.types.ObjectId

@Controller("/persons")
class PersonsController(
    private val personService: PersonService
) {
    @Get("/{id}")
    fun findById(@PathVariable("id") id: ObjectId): HttpResponse<Any> {
        val foundedPerson = personService.findById(id)
        return if (foundedPerson != null) {
            HttpResponse.ok(FindPersonByIdResponse(foundedPerson))
        } else {
            HttpResponse.notFound()
        }
    }
}