package com.bank.controller

import com.bank.model.Person
import com.bank.repository.PersonRepository
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get

@Controller("/persons")
class PersonsController(
    val personRepository: PersonRepository
) {
    @Get
    fun findAll(): MutableIterable<Person> {
        return personRepository.findAll()
    }
}