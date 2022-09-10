package com.bank.adapters.repository

import com.bank.domain.model.Person
import io.micronaut.data.mongodb.annotation.MongoRepository
import io.micronaut.data.repository.CrudRepository
import org.bson.types.ObjectId

@MongoRepository
interface MicronautDataRepository : CrudRepository<Person, ObjectId>{

    fun findByCpf(cpf: String): Person?
}