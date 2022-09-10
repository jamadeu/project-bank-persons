package com.bank.application.exception

class PersonAlreadyExistsException(override val message: String?) : RuntimeException(message)
