package com.bank.application.service

import com.bank.domain.repository.PersonRepository
import com.bank.domain.service.PersonService
import jakarta.inject.Singleton

@Singleton
class PersonServiceImpl(override val personRepository: PersonRepository) : PersonService(personRepository)