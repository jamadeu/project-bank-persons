package com.bank.application.service

import com.bank.domain.exception.PersonNotFoundException
import com.bank.domain.model.Person
import com.bank.domain.repository.PersonRepository
import com.bank.domain.service.PersonService
import jakarta.inject.Singleton

@Singleton
class PersonServiceImpl(private val personRepository: PersonRepository) : PersonService {
    override fun findById(id: String): Person {
        return personRepository.findById(id) ?: throw PersonNotFoundException("Person with id $id not found")
    }

    override fun save(person: Person): Person {
        return personRepository.save(person)
    }

    override fun update(person: Person): Person {
        return personRepository.save(person)
    }

    override fun deleteById(id: String) {
        personRepository.deleteById(id)
    }
}