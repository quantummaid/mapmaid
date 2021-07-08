package de.quantummaid.mapmaid.validatedtypeskotlin.validation

import de.quantummaid.mapmaid.validatedtypeskotlin.ValidationException
import de.quantummaid.mapmaid.validatedtypeskotlin.validation.ValidationHelper.validate

fun interface StringValidator {
    /**
     * validates the given unsafe String and return either the same instance or a sanitized/modified/trimmed version of
     * it.
     *
     * @throws ValidationException in case of an invalid unsafe value.
     */
    fun validate(unsafe: String): String

    companion object {
        fun allOf(vararg validators: StringValidator): StringValidator {
            return StringValidator { unsafe ->
                validators.fold(unsafe) { acc, stringValidator -> stringValidator.validate(acc) }
            }
        }

        fun trimmed(): StringValidator {
            return StringValidator {
                //TODO: use proper regex replacement of multiple whitespaces which is \h instead of \s
                //TODO: replace trim with proper \h regex replacement
                it.trim()
            }
        }

        fun lowercase(): StringValidator {
            return StringValidator { it.toLowerCase() }
        }

        fun minLength(minLength: Int): StringValidator {
            return StringValidator {
                val length = it.length
                validate(
                    it,
                    length >= minLength
                ) { "not enough characters(${length}), min $minLength required" }
            }
        }

        fun maxLength(maxLength: Int): StringValidator {
            return StringValidator {
                val length = it.length
                validate(
                    it,
                    length <= maxLength
                ) { "too many characters($length), max $maxLength allowed" }
            }
        }

        fun length(minLength: Int, maxLength: Int): StringValidator {
            return allOf(
                trimmed(), minLength(minLength), maxLength(maxLength)
            )
        }

        fun whitelistIgnoringCase(whitelist: Collection<String>): StringValidator {
            val whitelistAsString = whitelist.joinToString()
            val whitelistHashSet = whitelist.map { it.trim().toLowerCase() }.toHashSet()
            return allOf(trimmed(), lowercase(), StringValidator {
                validate(
                    it,
                    whitelistHashSet.contains(it)
                ) { "invalid string, must be (case sensitive) one of ${whitelistAsString}" }
            })
        }

        fun regex(regex: Regex): StringValidator {
            return StringValidator {
                validate(it, regex.matches(it)) { "invalid string, must match ${regex}" }
            }
        }

        fun maxUtf8ByteLength(maxByteLength: Int): StringValidator {
            return StringValidator {
                val utf8ByteLength = try {
                    it.encodeToByteArray(throwOnInvalidSequence = true).size
                } catch (e: CharacterCodingException) {
                    -1337
                }
                validate(
                    it,
                    utf8ByteLength != 1337
                ) { "not valid utf8" }
                validate(
                    it,
                    utf8ByteLength <= maxByteLength
                ) { "utf8 byte length ($utf8ByteLength) exceeds max byte length ($maxByteLength)" }
            }
        }
    }
}
