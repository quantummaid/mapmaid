package de.quantummaid.mapmaid.validatedtypeskotlin.types

import de.quantummaid.mapmaid.validatedtypeskotlin.validation.StringValidator

abstract class ValidatedString private constructor(
    protected open val safeValue: String
) : ValueType<String> {
    constructor(validator: StringValidator, unsafeValue: String) : this(validator.validate(unsafeValue))

    override fun mappingValue(): String {
        return safeValue
    }

    override fun toString(): String {
        return safeValue
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        if (this::class.java != other::class.java) return false

        other as ValidatedString

        if (mappingValue() != other.mappingValue()) return false

        return true
    }

    override fun hashCode(): Int {
        return safeValue.hashCode()
    }
}
