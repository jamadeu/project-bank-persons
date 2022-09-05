package com.bank.adapters.controller

import com.bank.adapters.repository.MicronautDataRepository
import com.bank.domain.repository.PersonRepository
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
@MicronautTest
internal class PersonsControllerTest(private val personRepository: PersonRepository) {

    @Inject
    lateinit var micronautDataRepository: MicronautDataRepository

    @Container
    private val mongoContainer: MongoDBContainer =
        MongoDBContainer("mongo:4.0")
            .withExposedPorts(27017)

    @BeforeAll
    fun startDb() {
        mongoContainer.start()
    }

    @BeforeEach
    fun setup() {
        micronautDataRepository.deleteAll()
    }

    @AfterAll
    fun cleanUp() {
        mongoContainer.close()
    }

}