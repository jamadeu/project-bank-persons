package com.bank.adapters.controller.dto

import com.bank.domain.model.Person
import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDate

data class FindPersonByIdResponse(
    var id: String,
    var name: String,
    var cpf: String,
    var address: String,
    @JsonFormat(pattern = "yyyy-MM-dd")
    var createdAt: LocalDate,
    @JsonFormat(pattern = "yyyy-MM-dd")
    var updatedAt: LocalDate,
) {
    companion object {
        fun fromEntity(person: Person): FindPersonByIdResponse =
            FindPersonByIdResponse(
                id = person.id!!.toHexString(),
                name = person.name,
                cpf = person.cpf,
                address = person.address,
                createdAt = person.createdAt!!,
                updatedAt = person.updatedAt!!
            )
    }
}
