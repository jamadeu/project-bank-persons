package com.bank.adapters.controller

import com.bank.adapters.controller.dto.CreatePersonRequest
import com.bank.adapters.controller.dto.FindPersonByIdResponse
import com.bank.adapters.repository.MicronautDataRepository
import com.bank.domain.model.Person
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import io.micronaut.test.support.TestPropertyProvider
import jakarta.inject.Inject
import org.junit.jupiter.api.*
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import java.util.*
import kotlin.reflect.jvm.internal.impl.load.kotlin.JvmType

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@MicronautTest
internal class PersonsControllerTest : TestPropertyProvider {

    @Inject
    lateinit var micronautDataRepository: MicronautDataRepository

    @Inject
    @field:Client("/")
    lateinit var client: HttpClient

    @Container
    private val mongoDBContainer: MongoDBContainer =
        MongoDBContainer(DockerImageName.parse("mongo:4.0.10"))
            .apply { start() }

    @BeforeEach
    fun setup() {
        micronautDataRepository.deleteAll()
    }

    @AfterAll
    fun cleanUp() {
        mongoDBContainer.close()
    }

    @Test
    fun `findById must return not found when person does not exists`() {
        val id = 1
        client.toBlocking().run {
            assertThrows<HttpClientResponseException> {
                exchange<Unit, String>(
                    HttpRequest.GET<Unit?>("/persons/$id")
                ).also {
                    assert(HttpStatus.NOT_FOUND == it.status)
                    assert(it.body() != null)
                    assert(it.body()!!.contains("Person with id $id not found"))
                }
            }
        }
        assert(micronautDataRepository.findAll().count() == 0)
    }

    @Test
    fun `findById must return a person when success`() {
        val person = getPerson()
        val id = getSavedPersonId(person)
        val response = client.toBlocking().exchange<Unit, FindPersonByIdResponse>(
            HttpRequest.GET("/persons/$id"), FindPersonByIdResponse::class.java
        )

        assert(HttpStatus.OK == response.status)
        assert(response.body.isPresent)
        with(response.body.get()){
            assert(this.id == id)
            assert(this.name == person.name)
            assert(this.cpf == person.cpf)
            assert(this.address == person.address)
        }
        assert(micronautDataRepository.findAll().count() == 1)
    }

    private fun getSavedPersonId(personToCreate: Person): String {
        val createPersonRequest = CreatePersonRequest(
            name = personToCreate.name,
            cpf = personToCreate.cpf,
            address = personToCreate.address
        )
        return client
            .toBlocking()
            .exchange<CreatePersonRequest, String>(
                HttpRequest.POST("/persons", createPersonRequest)
            ).header("location")!!
    }

    private fun getPerson(
        name: String = "Test",
        cpf: String = "729.934.180-84",
        address: String = "Adrress"
    ) = Person(name, cpf, address)

    override fun getProperties(): MutableMap<String, String> {
        return Collections.singletonMap("mongodb.uri", mongoDBContainer.replicaSetUrl)
    }

}