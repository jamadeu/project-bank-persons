package com.bank.adapters.input.controller.dto

import com.bank.domain.model.Person
import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.NotBlank

@Introspected
data class CreatePersonRequest(
    @field:NotBlank
    val name: String?,
    @field:NotBlank
    val cpf: String?,
    @field:NotBlank
    val address: String?,
) {
    fun toModel(): Person =
        Person(
            name = this.name ?: throw RuntimeException("CreatePersonRequest toModel() - name is null"),
            cpf = this.cpf ?: throw RuntimeException("CreatePersonRequest toModel() - CPF is null"),
            address = this.address ?: throw RuntimeException("CreatePersonRequest toModel() - address is null")
        )
}
