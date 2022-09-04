package com.bank.domain.service

import com.bank.domain.model.Person
import com.bank.domain.repository.PersonRepository
import jakarta.inject.Singleton
import org.bson.types.ObjectId


open class PersonService(
    open val personRepository: PersonRepository
) {
    fun findById(id: ObjectId): Person? {
        return personRepository.findById(id)
    }

    fun save(person: Person): Person{
        return personRepository.save(person)
    }

    fun update(person: Person): Person{
        return personRepository.update(person)
    }

    fun deleteById(id: ObjectId){
        personRepository.deleteById(id)
    }
}