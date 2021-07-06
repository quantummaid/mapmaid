package de.quantummaid.mapmaid.validatedtypeskotlin.types

import de.quantummaid.mapmaid.validatedtypeskotlin.ValidationException
import de.quantummaid.mapmaid.validatedtypeskotlin.validation.StringValidator
import de.quantummaid.mapmaid.validatedtypeskotlin.validation.StringValidator.Companion.length
import java.net.URL

abstract class ValidatedUrl(unsafe: String) : ValidatedString(url(), unsafe) {
    companion object {
        private fun url(): StringValidator {
            return StringValidator.allOf(
                length(4, 256),
                StringValidator {
                    try {
                        URL(it)
                        it
                    } catch (e: Exception) {
                        throw ValidationException("invalid url", e)
                    }
                }
            )
        }
    }
}
