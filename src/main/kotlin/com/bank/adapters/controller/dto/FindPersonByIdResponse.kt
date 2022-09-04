package com.bank.adapters.controller.dto

import com.bank.domain.model.Address
import com.bank.domain.model.Person
import com.fasterxml.jackson.annotation.JsonFormat
import org.bson.types.ObjectId
import java.time.LocalDate

data class FindPersonByIdResponse(
    var id: ObjectId,
    var name: String,
    var CPF: String,
    var address: AddressDto,
    @JsonFormat(pattern = "yyyy-MM-dd")
    var createdAt: LocalDate,
    @JsonFormat(pattern = "yyyy-MM-dd")
    var updatedAt: LocalDate,
) {
    constructor(person: Person) : this(
        id = person.id,
        name = person.name,
        CPF = person.CPF,
        address = AddressDto(person.address),
        createdAt = person.createdAt,
        updatedAt = person.updatedAt
    )
}

data class AddressDto(
    val street: String,
    val number: String,
    val state: String,
    val country: String
) {
    constructor(address: Address) : this(
        street = address.street,
        number = address.number,
        state = address.state,
        country = address.country
    )
}




