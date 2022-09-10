package com.bank.adapters.input.controller.dto

import com.bank.domain.model.Person
import com.fasterxml.jackson.annotation.JsonFormat
import io.micronaut.core.annotation.Introspected
import org.bson.types.ObjectId
import java.time.LocalDate
import javax.validation.constraints.NotBlank

@Introspected
data class UpdatePersonRequest(
    @field:NotBlank
    var id: String?,
    @field:NotBlank
    var name: String?,
    @field:NotBlank
    var cpf: String?,
    @field:NotBlank
    var address: String?,
    @field:NotBlank
    @JsonFormat(pattern = "yyyy-MM-dd")
    var createdAt: LocalDate?,
    @field:NotBlank
    @JsonFormat(pattern = "yyyy-MM-dd")
    var updatedAt: LocalDate?,
) {
    fun toModel(): Person {
        val person = Person(
            name = this.name ?: throw RuntimeException("CreatePersonRequest toModel() - name is null"),
            cpf = this.cpf ?: throw RuntimeException("CreatePersonRequest toModel() - cpf is null"),
            address = this.address ?: throw RuntimeException("CreatePersonRequest toModel() - address is null")
        )
        person.id = ObjectId(id ?: throw RuntimeException("CreatePersonRequest toModel() - id is null"))
        person.createdAt = createdAt ?: throw RuntimeException("CreatePersonRequest toModel() - createdAt is null")
        person.updatedAt = updatedAt ?: throw RuntimeException("CreatePersonRequest toModel() - updatedAt is null")
        return person
    }
}
