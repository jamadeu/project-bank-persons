package com.bank.adapters.controller.dto

import com.bank.domain.model.Address
import com.bank.domain.model.Person
import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDate

data class FindPersonByIdResponse(
    var id: String,
    var name: String,
    var CPF: String,
    var address: AddressDto,
    @JsonFormat(pattern = "yyyy-MM-dd")
    var createdAt: LocalDate,
    @JsonFormat(pattern = "yyyy-MM-dd")
    var updatedAt: LocalDate,
) {
    companion object {
        fun fromEntity(person: Person): FindPersonByIdResponse =
            FindPersonByIdResponse(
                id = person.id.toHexString(),
                name = person.name,
                CPF = person.CPF,
                address = AddressDto.fromEntity(person.address),
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
        companion object {
            fun fromEntity(address: Address): AddressDto =
                AddressDto(
                    street = address.street,
                    number = address.number,
                    state = address.state,
                    country = address.country
                )
        }
    }
}
