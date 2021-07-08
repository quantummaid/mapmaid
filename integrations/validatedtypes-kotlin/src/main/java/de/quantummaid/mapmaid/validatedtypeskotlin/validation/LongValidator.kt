package de.quantummaid.mapmaid.validatedtypeskotlin.validation

import de.quantummaid.mapmaid.validatedtypeskotlin.ValidationException
import de.quantummaid.mapmaid.validatedtypeskotlin.validation.ValidationHelper.validate

fun interface LongValidator {
    /**
     * validates the given unsafe Long and return either the same instance or a sanitized/modified version of it.
     *
     * @throws ValidationException in case of an invalid unsafe value.
     */
    fun validate(unsafe: Long): Long

    companion object {
        fun parseLong(unsafe: String): Long {
            try {
                return unsafe.toLong()
            } catch (e: Exception) {
                throw ValidationException("must be a valid integer number")
            }
        }

        fun allOf(vararg validators: LongValidator): LongValidator {
            return LongValidator { unsafe ->
                validators.fold(unsafe) { acc, longValidator -> longValidator.validate(acc) }
            }
        }

        fun min(min: Int): LongValidator {
            return LongValidator {
                validate(
                    it,
                    it >= min
                ) { "must be greater equal $min" }
            }
        }

        fun max(max: Int): LongValidator {
            return LongValidator {
                validate(
                    it,
                    it <= max
                ) { "must be smaller equal $max" }
            }
        }

        fun interval(min: Long, max: Long): LongValidator {
            return LongValidator {
                validate(
                    it,
                    it in min..max
                ) { "must be between $min and $max (inclusive)" }
            }
        }
    }
}
