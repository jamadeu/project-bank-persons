package com.bank.domain.model

import com.fasterxml.jackson.annotation.JsonFormat
import io.micronaut.data.annotation.*
import org.bson.types.ObjectId
import java.time.LocalDate

@MappedEntity
data class Person(

    val name: String,
    val cpf: String,
    val address: String,
) {

    @Id
    @GeneratedValue
    var id: ObjectId? = null

    @DateCreated
    @JsonFormat(pattern = "yyyy-MM-dd")
    var createdAt: LocalDate? = LocalDate.now()

    @DateUpdated
    @JsonFormat(pattern = "yyyy-MM-dd")
    var updatedAt: LocalDate? = LocalDate.now()
}
