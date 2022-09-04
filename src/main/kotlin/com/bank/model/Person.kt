package com.bank.model

import com.fasterxml.jackson.annotation.JsonFormat
import io.micronaut.data.annotation.GeneratedValue
import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import org.bson.types.ObjectId
import java.time.DateTimeException
import java.time.LocalDate

@MappedEntity
data class Person(
    @field:Id
    @GeneratedValue
    val id: ObjectId,
    val name: String,
    val CPF: String,
    val address: Address,
    @JsonFormat(pattern = "yyyy-MM-dd")
    val createdAt: LocalDate = LocalDate.now(),
    @JsonFormat(pattern = "yyyy-MM-dd")
    val updatedAt: LocalDate = LocalDate.now(),
)

data class Address(
    val street: String,
    val number: String,
    val state: String,
    val country: String
)
