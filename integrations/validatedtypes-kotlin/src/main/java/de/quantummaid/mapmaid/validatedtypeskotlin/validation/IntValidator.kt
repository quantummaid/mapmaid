package de.quantummaid.mapmaid.validatedtypeskotlin.validation

import de.quantummaid.mapmaid.validatedtypeskotlin.ValidationException
import de.quantummaid.mapmaid.validatedtypeskotlin.validation.ValidationHelper.validate

fun interface IntValidator {
    /**
     * validates the given unsafe Int and return either the same instance or a sanitized/modified version of it.
     *
     * @throws ValidationException in case of an invalid unsafe value.
     */
    fun validate(unsafe: Int): Int

    companion object {
        fun parseInt(unsafe: String): Int {
            try {
                return unsafe.toInt()
            } catch (e: Exception) {
                throw ValidationException("must be a valid integer number")
            }
        }

        fun allOf(vararg validators: IntValidator): IntValidator {
            return IntValidator { unsafe ->
                validators.fold(unsafe) { acc, intValidator -> intValidator.validate(acc) }
            }
        }

        fun min(min: Int): IntValidator {
            return IntValidator {
                validate(
                    it,
                    it >= min
                ) { "must be greater equal $min" }
            }
        }

        fun max(max: Int): IntValidator {
            return IntValidator {
                validate(
                    it,
                    it <= max
                ) { "must be smaller equal $max" }
            }
        }

        fun interval(min: Int, max: Int): IntValidator {
            return IntValidator {
                validate(
                    it,
                    it in min..max
                ) { "must be between $min and $max (inclusive)" }
            }
        }
    }
}
