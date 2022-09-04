package com.bank.adapters.repository

import com.bank.domain.model.Person
import com.bank.domain.repository.PersonRepository
import jakarta.inject.Singleton
import org.bson.types.ObjectId

@Singleton
class PersonRepositoryImpl(
    private val repository: MicronautDataRepository
) : PersonRepository {
    override fun findById(id: ObjectId): Person? {
        return repository.findById(id).orElse(null)
    }

    override fun save(person: Person): Person {
        return repository.save(person)
    }

    override fun update(person: Person): Person {
        return repository.save(person)
    }

    override fun deleteById(id: ObjectId) {
        repository.deleteById(id)
    }

}