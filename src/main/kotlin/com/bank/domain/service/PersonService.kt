package com.bank.domain.service

import com.bank.domain.model.Person

interface PersonService {
    fun findById(id: String): Person?

    fun save(person: Person): Person

    fun update(person: Person): Person

    fun deleteById(id: String)
}