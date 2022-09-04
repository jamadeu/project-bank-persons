package com.bank.domain.service

import com.bank.domain.model.Person
import org.bson.types.ObjectId

interface PersonService {
    fun findById(id: ObjectId): Person?

    fun save(person: Person): Person

    fun update(person: Person): Person

    fun deleteById(id: ObjectId)
}