package de.quantummaid.mapmaid.validatedtypeskotlin.validation

import de.quantummaid.mapmaid.validatedtypeskotlin.ValidationException

object ValidationHelper {
    inline fun <T> validate(retVal: T, condition: Boolean, lazyMessage: () -> String): T {
        if (!condition) {
            val exception = ValidationException(lazyMessage())
            throw exception
        } else {
            return retVal
        }
    }
}
