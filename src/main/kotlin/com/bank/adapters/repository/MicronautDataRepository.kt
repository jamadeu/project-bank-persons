package com.bank.adapters.repository

import com.bank.domain.model.Person
import io.micronaut.data.mongodb.annotation.MongoRepository
import io.micronaut.data.repository.CrudRepository
import org.bson.types.ObjectId
import java.util.*

@MongoRepository
interface MicronautDataRepository : CrudRepository<Person, ObjectId>