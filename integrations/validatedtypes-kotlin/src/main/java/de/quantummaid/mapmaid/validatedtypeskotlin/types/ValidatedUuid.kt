package de.quantummaid.mapmaid.validatedtypeskotlin.types

import de.quantummaid.mapmaid.validatedtypeskotlin.ValidationException
import de.quantummaid.mapmaid.validatedtypeskotlin.validation.StringValidator
import java.util.*

abstract class ValidatedUuid(unsafe: String) : ValidatedString(uuid(), unsafe) {
    companion object {
        private fun uuid(): StringValidator {
            return StringValidator.allOf(
                StringValidator.length(36, 36), StringValidator.lowercase(), StringValidator {
                    try {
                        UUID.fromString(it)
                        it
                    } catch (e: Exception) {
                        throw ValidationException("invalid uuid")
                    }
                }
            )
        }
    }
}
