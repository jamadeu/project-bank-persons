package com.bank.adapters.output.repository

import com.bank.domain.model.Person
import com.bank.domain.repository.PersonRepository
import jakarta.inject.Singleton
import org.bson.types.ObjectId

@Singleton
class PersonRepositoryImpl(
    private val repository: MicronautDataRepository
) : PersonRepository {
    override fun findById(id: String): Person? {
        return repository.findById(ObjectId(id)).orElse(null)
    }

    override fun save(person: Person): Person {
        return repository.save(person)
    }

    override fun update(person: Person): Person {
        return repository.update(person)
    }

    override fun deleteById(id: String) {
        repository.deleteById(ObjectId(id))
    }

    override fun findByCpf(cpf: String): Person? {
        return repository.findByCpf(cpf)
    }

}