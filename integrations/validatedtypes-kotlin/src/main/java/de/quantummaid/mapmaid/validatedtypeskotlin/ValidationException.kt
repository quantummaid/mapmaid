package de.quantummaid.mapmaid.validatedtypeskotlin

class ValidationException : Exception {
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable?) : super(message, cause)
}
