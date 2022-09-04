package com.bank.repository

import com.bank.model.Person
import io.micronaut.data.mongodb.annotation.MongoRepository
import io.micronaut.data.repository.CrudRepository
import org.bson.types.ObjectId

@MongoRepository
interface PersonRepository : CrudRepository<Person, ObjectId>