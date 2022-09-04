package com.bank.domain.exception

class PersonNotFoundException(override val message: String) : RuntimeException(message)