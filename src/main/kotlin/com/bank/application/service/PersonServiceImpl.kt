package com.bank.application.service

import com.bank.domain.exception.CpfCantBeChangedException
import com.bank.domain.exception.InvalidCpfException
import com.bank.domain.exception.PersonAlreadyExistsException
import com.bank.domain.exception.PersonNotFoundException
import com.bank.domain.model.Person
import com.bank.domain.repository.PersonRepository
import com.bank.domain.service.PersonService
import io.micronaut.http.annotation.Produces
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory

@Produces
@Singleton
class PersonServiceImpl(private val personRepository: PersonRepository) : PersonService {

    private val logger = LoggerFactory.getLogger(javaClass)
    override fun findById(id: String): Person {
        logger.info("PersonServiceImpl - findById")
        return personRepository.findById(id) ?: throw PersonNotFoundException("Person with id $id not found")
    }

    override fun findByCpf(cpf: String): Person {
        logger.info("PersonServiceImpl - findByCpf")
        if (!isCPF(cpf)) {
            logger.error("PersonServiceImpl - save, invalid cpf - $cpf")
            throw InvalidCpfException("Cpf is invalid")
        }
        return personRepository.findByCpf(cpf) ?: throw PersonNotFoundException("Person with cpf $cpf not found")
    }

    override fun save(person: Person): Person {
        logger.info("PersonServiceImpl - save, person $person")
        if (!isCPF(person.cpf)) {
            logger.error("PersonServiceImpl - save, invalid cpf - ${person.cpf}")
            throw InvalidCpfException("Cpf is invalid")
        }
        personRepository.findByCpf(person.cpf)
            .also { person ->
                if (person != null) {
                    logger.error("PersonServiceImpl - Person with cpf ${person.cpf} already exists")
                    throw PersonAlreadyExistsException("Person with cpf ${person.cpf} already exists")
                }
            }
        return personRepository.save(person)
    }

    override fun update(person: Person): Person {
        logger.info("PersonServiceImpl - update, person $person")
        this.findById(person.id.toString()).also { p ->
            logger.error("PersonServiceImpl - update, cpf cannot be changed")
            if (p.cpf != person.cpf) throw CpfCantBeChangedException("Cpf cannot be changed")
        }
        return personRepository.update(person)
    }

    override fun deleteById(id: String) {
        personRepository.deleteById(id)
    }

    private fun isCPF(document: String): Boolean {
        if (document.isEmpty()) return false

        val numbers = document.filter { it.isDigit() }.map {
            it.toString().toInt()
        }

        if (numbers.size != 11) return false

        //repeticao
        if (numbers.all { it == numbers[0] }) return false

        //digito 1
        val dv1 = ((0..8).sumOf { (it + 1) * numbers[it] }).rem(11).let {
            if (it >= 10) 0 else it
        }

        val dv2 = ((0..8).sumOf { it * numbers[it] }.let { (it + (dv1 * 9)).rem(11) }).let {
            if (it >= 10) 0 else it
        }

        return numbers[9] == dv1 && numbers[10] == dv2
    }
}