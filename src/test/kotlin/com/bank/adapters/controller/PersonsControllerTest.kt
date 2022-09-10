package com.bank.adapters.controller

import com.bank.adapters.controller.dto.CreatePersonRequest
import com.bank.adapters.controller.dto.FindPersonByCpfResponse
import com.bank.adapters.controller.dto.FindPersonByIdResponse
import com.bank.adapters.controller.dto.UpdatePersonRequest
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
import org.bson.types.ObjectId
import org.junit.jupiter.api.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EmptySource
import org.junit.jupiter.params.provider.NullSource
import org.junit.jupiter.params.provider.ValueSource
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import java.time.LocalDate
import java.util.*


@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@MicronautTest
internal class PersonsControllerTest : TestPropertyProvider {

    @Inject
    lateinit var micronautDataRepository: MicronautDataRepository

    @Inject
    @field:Client("/")
    lateinit var client: HttpClient

    private val mongoDBContainer: MongoDBContainer =
        MongoDBContainer(DockerImageName.parse("mongo:4.0.10"))
            .apply { start() }

    @BeforeEach
    fun setup() {
        micronautDataRepository.deleteAll()
    }

    @AfterAll
    fun close() {
        mongoDBContainer.close()
    }

    @Test
    fun `findById must return not found when person does not exists`() {
        val id = "63189af4e1a7f0759d06aa8b"
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
    }

    @Test
    fun `findById must return a person when success`() {
        val person = getPerson()
        createPerson(person)
        val id = getSavedPersonId(person)
        val response = client.toBlocking().exchange<Unit, FindPersonByIdResponse>(
            HttpRequest.GET("/persons/$id"), FindPersonByIdResponse::class.java
        )

        assert(HttpStatus.OK == response.status)
        assert(response.body.isPresent)
        with(response.body.get()) {
            assert(this.id == id)
            assert(this.name == person.name)
            assert(this.cpf == person.cpf)
            assert(this.address == person.address)
        }
    }

    @Test
    fun `findByCpf must return not found when person does not exists`() {
        val cpf = "691.801.660-16"
        client.toBlocking().run {
            assertThrows<HttpClientResponseException> {
                exchange<Unit, String>(
                    HttpRequest.GET<Unit?>("/persons/cpf/$cpf")
                ).also {
                    assert(HttpStatus.NOT_FOUND == it.status)
                    assert(it.body() != null)
                    assert(it.body()!!.contains("Person with cpf $cpf not found"))
                }
            }
        }
    }

    @Test
    fun `findByCpf must return bad request when cpf is invalid`() {
        val cpf = "111.111.111-11"
        client.toBlocking().run {
            assertThrows<HttpClientResponseException> {
                exchange<Unit, String>(
                    HttpRequest.GET<Unit?>("/persons/cpf/$cpf")
                ).also {
                    assert(HttpStatus.BAD_REQUEST == it.status)
                    assert(it.body() != null)
                    assert(it.body()!!.contains("Cpf is invalid"))
                }
            }
        }
    }

    @Test
    fun `findByCpf must return a person when success`() {
        val person = getPerson()
        createPerson(person)
        getSavedPersonId(person)
        val response = client.toBlocking().exchange<Unit, FindPersonByCpfResponse>(
            HttpRequest.GET("/persons/cpf/${person.cpf}"), FindPersonByCpfResponse::class.java
        )

        assert(HttpStatus.OK == response.status)
        assert(response.body.isPresent)
        with(response.body.get()) {
            assert(this.id == id)
            assert(this.name == person.name)
            assert(this.cpf == person.cpf)
            assert(this.address == person.address)
        }
    }

    @Test
    fun `create must return created when successful`() {
        val personToCreate = getPerson()
        val createPersonRequest = CreatePersonRequest(
            name = personToCreate.name,
            cpf = personToCreate.cpf,
            address = personToCreate.address
        )
        val response = client
            .toBlocking()
            .exchange<CreatePersonRequest, String>(
                HttpRequest.POST("/persons", createPersonRequest)
            )

        assert(response.status == HttpStatus.CREATED)
        val personId = response.header("location")
        assert(personId != null)
        val createdPerson =
            micronautDataRepository
                .findById(ObjectId(personId))
                .run {
                    assert(this.isPresent)
                    this.get()
                }
        assert(createdPerson.cpf == personToCreate.cpf)
        assert(createdPerson.name == personToCreate.name)
        assert(createdPerson.address == personToCreate.address)
    }

    @Test
    fun `create must return bad request when person already exists`() {
        val personToCreate = getPerson()
        createPerson(personToCreate)
        val createPersonRequest = CreatePersonRequest(
            name = personToCreate.name,
            cpf = personToCreate.cpf,
            address = personToCreate.address
        )
        client.toBlocking().run {
            assertThrows<HttpClientResponseException> {
                exchange<CreatePersonRequest, String>(
                    HttpRequest.POST("/persons", createPersonRequest)
                ).also {
                    assert(HttpStatus.BAD_REQUEST == it.status)
                    assert(it.body() != null)
                    assert(it.body()!!.contains("Person with cpf ${personToCreate.cpf} already exists"))
                }
            }
        }
    }

    @ParameterizedTest
    @NullSource
    @EmptySource
    fun `create must return bad request when name is null or blank`(name: String?) {
        val personToCreate = getPerson()
        val createPersonRequest = CreatePersonRequest(
            name = name,
            cpf = personToCreate.cpf,
            address = personToCreate.address
        )
        client.toBlocking().run {
            assertThrows<HttpClientResponseException> {
                exchange<CreatePersonRequest, String>(
                    HttpRequest.POST("/persons", createPersonRequest)
                ).also {
                    assert(HttpStatus.BAD_REQUEST == it.status)
                }
            }
        }
    }

    @ParameterizedTest
    @NullSource
    @EmptySource
    fun `create must return bad request when address is null or blank`(address: String?) {
        val personToCreate = getPerson()
        val createPersonRequest = CreatePersonRequest(
            name = personToCreate.name,
            cpf = personToCreate.cpf,
            address = address
        )
        client.toBlocking().run {
            assertThrows<HttpClientResponseException> {
                exchange<CreatePersonRequest, String>(
                    HttpRequest.POST("/persons", createPersonRequest)
                ).also {
                    assert(HttpStatus.BAD_REQUEST == it.status)
                }
            }
        }
    }

    @ParameterizedTest
    @NullSource
    @EmptySource
    @ValueSource(strings = ["111.111.111-11"])
    fun `create must return bad request when cpf is null or blank`(cpf: String?) {
        val personToCreate = getPerson()
        val createPersonRequest = CreatePersonRequest(
            name = personToCreate.name,
            cpf = cpf,
            address = personToCreate.address
        )
        client.toBlocking().run {
            assertThrows<HttpClientResponseException> {
                exchange<CreatePersonRequest, String>(
                    HttpRequest.POST("/persons", createPersonRequest)
                ).also {
                    assert(HttpStatus.BAD_REQUEST == it.status)
                }
            }
        }
    }

    @Test
    fun `update must return no content when successful`() {
        val newName = "New name"
        val personToCreate = getPerson()
        createPerson(personToCreate)
        val person = micronautDataRepository.findByCpf(personToCreate.cpf)!!
        val request = UpdatePersonRequest(
            id = person.id.toString(),
            name = newName,
            cpf = person.cpf,
            address = person.address,
            createdAt = person.createdAt,
            updatedAt = person.updatedAt
        )

        client.toBlocking()
            .exchange<UpdatePersonRequest, String>(
                HttpRequest.PUT("/persons", request)
            ).also {
                assert(HttpStatus.NO_CONTENT == it.status)
            }

        val updatedPerson = client.toBlocking().exchange<Unit, FindPersonByCpfResponse>(
            HttpRequest.GET("/persons/cpf/${person.cpf}"), FindPersonByCpfResponse::class.java
        ).body.get()

        assert(updatedPerson.name == newName)
        assert(updatedPerson.cpf == person.cpf)
        assert(updatedPerson.address == person.address)
    }

    @Test
    fun `update must return bad request when cpf is changed`() {
        val personToCreate = getPerson()
        createPerson(personToCreate)
        val person = micronautDataRepository.findByCpf(personToCreate.cpf)!!
        val request = UpdatePersonRequest(
            id = person.id.toString(),
            name = person.name,
            cpf = "526.815.140-10",
            address = person.address,
            createdAt = person.createdAt,
            updatedAt = person.updatedAt
        )

        client.toBlocking().run {
            assertThrows<HttpClientResponseException> {
                exchange<UpdatePersonRequest, String>(
                    HttpRequest.PUT("/persons", request)
                ).also {
                    assert(HttpStatus.BAD_REQUEST == it.status)
                }
            }
        }


        val updatedPerson = client.toBlocking().exchange<Unit, FindPersonByCpfResponse>(
            HttpRequest.GET("/persons/cpf/${person.cpf}"), FindPersonByCpfResponse::class.java
        ).body.get()

        assert(updatedPerson.name == person.name)
        assert(updatedPerson.cpf == person.cpf)
        assert(updatedPerson.address == person.address)
        assert(updatedPerson.createdAt == person.createdAt)
        assert(updatedPerson.updatedAt == person.updatedAt)
    }

    @ParameterizedTest
    @NullSource
    @EmptySource
    @ValueSource(strings = ["111.111.111-11"])
    fun `update must return bad request when cpf is null or blank`(cpf: String?) {
        val personToCreate = getPerson()
        createPerson(personToCreate)
        val person = micronautDataRepository.findByCpf(personToCreate.cpf)!!
        val request = UpdatePersonRequest(
            id = person.id.toString(),
            name = person.name,
            cpf = cpf,
            address = person.address,
            createdAt = person.createdAt,
            updatedAt = person.updatedAt
        )

        client.toBlocking().run {
            assertThrows<HttpClientResponseException> {
                exchange<UpdatePersonRequest, String>(
                    HttpRequest.PUT("/persons", request)
                ).also {
                    assert(HttpStatus.BAD_REQUEST == it.status)
                }
            }
        }


        val updatedPerson = client.toBlocking().exchange<Unit, FindPersonByCpfResponse>(
            HttpRequest.GET("/persons/cpf/${person.cpf}"), FindPersonByCpfResponse::class.java
        ).body.get()

        assert(updatedPerson.name == person.name)
        assert(updatedPerson.cpf == person.cpf)
        assert(updatedPerson.address == person.address)
        assert(updatedPerson.createdAt == person.createdAt)
        assert(updatedPerson.updatedAt == person.updatedAt)
    }

    @ParameterizedTest
    @NullSource
    @EmptySource
    fun `update must return bad request when name is null or blank`(name: String?) {
        val personToCreate = getPerson()
        createPerson(personToCreate)
        val person = micronautDataRepository.findByCpf(personToCreate.cpf)!!
        val request = UpdatePersonRequest(
            id = person.id.toString(),
            name = name,
            cpf = person.cpf,
            address = person.address,
            createdAt = person.createdAt,
            updatedAt = person.updatedAt
        )

        client.toBlocking().run {
            assertThrows<HttpClientResponseException> {
                exchange<UpdatePersonRequest, String>(
                    HttpRequest.PUT("/persons", request)
                ).also {
                    assert(HttpStatus.BAD_REQUEST == it.status)
                }
            }
        }


        val updatedPerson = client.toBlocking().exchange<Unit, FindPersonByCpfResponse>(
            HttpRequest.GET("/persons/cpf/${person.cpf}"), FindPersonByCpfResponse::class.java
        ).body.get()

        assert(updatedPerson.name == person.name)
        assert(updatedPerson.cpf == person.cpf)
        assert(updatedPerson.address == person.address)
        assert(updatedPerson.createdAt == person.createdAt)
        assert(updatedPerson.updatedAt == person.updatedAt)
    }

    @ParameterizedTest
    @NullSource
    @EmptySource
    fun `update must return bad request when address is null or blank`(address: String?) {
        val personToCreate = getPerson()
        createPerson(personToCreate)
        val person = micronautDataRepository.findByCpf(personToCreate.cpf)!!
        val request = UpdatePersonRequest(
            id = person.id.toString(),
            name = person.name,
            cpf = person.cpf,
            address = address,
            createdAt = person.createdAt,
            updatedAt = person.updatedAt
        )

        client.toBlocking().run {
            assertThrows<HttpClientResponseException> {
                exchange<UpdatePersonRequest, String>(
                    HttpRequest.PUT("/persons", request)
                ).also {
                    assert(HttpStatus.BAD_REQUEST == it.status)
                }
            }
        }


        val updatedPerson = client.toBlocking().exchange<Unit, FindPersonByCpfResponse>(
            HttpRequest.GET("/persons/cpf/${person.cpf}"), FindPersonByCpfResponse::class.java
        ).body.get()

        assert(updatedPerson.name == person.name)
        assert(updatedPerson.cpf == person.cpf)
        assert(updatedPerson.address == person.address)
        assert(updatedPerson.createdAt == person.createdAt)
        assert(updatedPerson.updatedAt == person.updatedAt)
    }

    @ParameterizedTest
    @NullSource
    fun `update must return bad request when createdAt is null`(createdAt: LocalDate?) {
        val personToCreate = getPerson()
        createPerson(personToCreate)
        val person = micronautDataRepository.findByCpf(personToCreate.cpf)!!
        val request = UpdatePersonRequest(
            id = person.id.toString(),
            name = person.name,
            cpf = person.cpf,
            address = person.address,
            createdAt = createdAt,
            updatedAt = person.updatedAt
        )

        client.toBlocking().run {
            assertThrows<HttpClientResponseException> {
                exchange<UpdatePersonRequest, String>(
                    HttpRequest.PUT("/persons", request)
                ).also {
                    assert(HttpStatus.BAD_REQUEST == it.status)
                }
            }
        }


        val updatedPerson = client.toBlocking().exchange<Unit, FindPersonByCpfResponse>(
            HttpRequest.GET("/persons/cpf/${person.cpf}"), FindPersonByCpfResponse::class.java
        ).body.get()

        assert(updatedPerson.name == person.name)
        assert(updatedPerson.cpf == person.cpf)
        assert(updatedPerson.address == person.address)
        assert(updatedPerson.createdAt == person.createdAt)
        assert(updatedPerson.updatedAt == person.updatedAt)
    }

    @ParameterizedTest
    @NullSource
    fun `update must return bad request when updatedAt is null`(updatedAt: LocalDate?) {
        val personToCreate = getPerson()
        createPerson(personToCreate)
        val person = micronautDataRepository.findByCpf(personToCreate.cpf)!!
        val request = UpdatePersonRequest(
            id = person.id.toString(),
            name = person.name,
            cpf = person.cpf,
            address = person.address,
            createdAt = person.createdAt,
            updatedAt = updatedAt
        )

        client.toBlocking().run {
            assertThrows<HttpClientResponseException> {
                exchange<UpdatePersonRequest, String>(
                    HttpRequest.PUT("/persons", request)
                ).also {
                    assert(HttpStatus.BAD_REQUEST == it.status)
                }
            }
        }


        val updatedPerson = client.toBlocking().exchange<Unit, FindPersonByCpfResponse>(
            HttpRequest.GET("/persons/cpf/${person.cpf}"), FindPersonByCpfResponse::class.java
        ).body.get()

        assert(updatedPerson.name == person.name)
        assert(updatedPerson.cpf == person.cpf)
        assert(updatedPerson.address == person.address)
        assert(updatedPerson.createdAt == person.createdAt)
        assert(updatedPerson.updatedAt == person.updatedAt)
    }

    @ParameterizedTest
    @NullSource
    @EmptySource
    fun `update must return bad request when id is null or blank`(id: String?) {
        val personToCreate = getPerson()
        createPerson(personToCreate)
        val person = micronautDataRepository.findByCpf(personToCreate.cpf)!!
        val request = UpdatePersonRequest(
            id = id,
            name = person.name,
            cpf = person.cpf,
            address = person.address,
            createdAt = person.createdAt,
            updatedAt = person.updatedAt
        )

        client.toBlocking().run {
            assertThrows<HttpClientResponseException> {
                exchange<UpdatePersonRequest, String>(
                    HttpRequest.PUT("/persons", request)
                ).also {
                    assert(HttpStatus.BAD_REQUEST == it.status)
                }
            }
        }


        val updatedPerson = client.toBlocking().exchange<Unit, FindPersonByCpfResponse>(
            HttpRequest.GET("/persons/cpf/${person.cpf}"), FindPersonByCpfResponse::class.java
        ).body.get()

        assert(updatedPerson.name == person.name)
        assert(updatedPerson.cpf == person.cpf)
        assert(updatedPerson.address == person.address)
        assert(updatedPerson.createdAt == person.createdAt)
        assert(updatedPerson.updatedAt == person.updatedAt)
    }

    @Test
    fun `delete must return no content when successful`() {
        val personToCreate = getPerson()
        createPerson(personToCreate)
        val person = micronautDataRepository.findByCpf(personToCreate.cpf)!!

        client.toBlocking()
            .exchange<Unit, String>(
                HttpRequest.DELETE("/persons/${person.id}")
            ).also {
                assert(HttpStatus.OK == it.status)
            }

        client.toBlocking().run {
            assertThrows<HttpClientResponseException> {
                exchange<Unit, String>(
                    HttpRequest.GET<Unit?>("/persons/${person.id}")
                ).also {
                    assert(HttpStatus.NOT_FOUND == it.status)
                    assert(it.body() != null)
                    assert(it.body()!!.contains("Person with id ${person.id} not found"))
                }
            }
        }
    }

    @Test
    fun `delete must return no content when person does not exists`() {
        val id = "63189af4e1a7f0759d06aa8b"
        client.toBlocking()
            .exchange<Unit, String>(
                HttpRequest.DELETE("/persons/$id")
            ).also {
                assert(HttpStatus.OK == it.status)
            }
    }

    private fun getSavedPersonId(person: Person) = micronautDataRepository.findByCpf(person.cpf)!!.id.toString()

    private fun getPerson(
        name: String = "Test",
        cpf: String = "729.934.180-84",
        address: String = "Adrress"
    ) = Person(name, cpf, address)

    private fun createPerson(personToCreate: Person) {
        val createPersonRequest = CreatePersonRequest(
            name = personToCreate.name,
            cpf = personToCreate.cpf,
            address = personToCreate.address
        )
        client
            .toBlocking()
            .exchange<CreatePersonRequest, String>(
                HttpRequest.POST("/persons", createPersonRequest)
            )
    }

    override fun getProperties(): MutableMap<String, String> {
        return Collections.singletonMap("mongodb.uri", mongoDBContainer.replicaSetUrl)
    }
}