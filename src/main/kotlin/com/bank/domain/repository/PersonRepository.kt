package com.bank.domain.repository

import com.bank.domain.model.Person

interface PersonRepository {

    fun findById(id: String): Person?

    fun save(person: Person): Person

    fun update(person: Person): Person

    fun deleteById(id: String)

    fun findByCpf(cpf: String): Person?
}