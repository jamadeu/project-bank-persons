package com.bank.application.exception

class PersonNotFoundException(override val message: String) : RuntimeException(message)