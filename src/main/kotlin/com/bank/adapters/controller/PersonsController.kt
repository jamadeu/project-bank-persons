package com.bank.adapters.controller

import com.bank.adapters.controller.dto.CreatePersonRequest
import com.bank.adapters.controller.dto.FindPersonByCpfResponse
import com.bank.adapters.controller.dto.FindPersonByIdResponse
import com.bank.adapters.controller.dto.UpdatePersonRequest
import com.bank.domain.service.PersonService
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.*
import io.micronaut.validation.Validated
import org.slf4j.LoggerFactory
import java.net.URI
import javax.validation.Valid

@Validated
@Controller("/persons")
class PersonsController(
    private val personService: PersonService
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @Get("/{id}")
    fun findById(id: String): HttpResponse<FindPersonByIdResponse> {
        logger.info("PersonsController - findById, id - $id")
        return HttpResponse.ok(FindPersonByIdResponse.fromEntity(personService.findById(id)))
    }

    @Get("/cpf/{cpf}")
    fun findByCpf(cpf: String): HttpResponse<FindPersonByCpfResponse> {
        logger.info("PersonsController - findByCpf, cpf - $cpf")
        return HttpResponse.ok(FindPersonByCpfResponse.fromEntity(personService.findByCpf(cpf)))
    }

    @Post
    fun create(@Body @Valid request: CreatePersonRequest): HttpResponse<URI> {
        logger.info("PersonsController - create, request - $request")
        val createdPerson = personService.create(request.toModel())
        logger.info("PersonsController - create, person created - ${createdPerson.id}")
        return HttpResponse.created(URI.create(createdPerson.id.toString()))
    }

    @Put
    fun update(@Body @Valid request: UpdatePersonRequest): HttpResponse<Unit> {
        logger.info("PersonsController - update, request - $request")
        personService.update(request.toModel())
        logger.info("PersonsController - update, person updated")
        return HttpResponse.noContent()
    }

    @Delete("/{id}")
    fun delete(id: String): HttpResponse<Unit> {
        logger.info("PersonsController - delete, id - $id")
        personService.deleteById(id)
        logger.info("PersonsController - delete, OK")
        return HttpResponse.ok()
    }
}