package com.bank.application.service

import com.bank.domain.model.Person
import com.bank.domain.repository.PersonRepository
import com.bank.domain.service.PersonService
import jakarta.inject.Singleton
import org.bson.types.ObjectId

@Singleton
class PersonServiceImpl(private val personRepository: PersonRepository) : PersonService {
    override fun findById(id: ObjectId): Person? {
        return personRepository.findById(id)
    }

    override fun save(person: Person): Person {
        return personRepository.save(person)
    }

    override fun update(person: Person): Person {
        return personRepository.save(person)
    }

    override fun deleteById(id: ObjectId) {
        personRepository.deleteById(id)
    }
}