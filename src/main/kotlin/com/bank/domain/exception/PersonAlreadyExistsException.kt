package com.bank.domain.exception

class PersonAlreadyExistsException(override val message: String?) : RuntimeException(message)
