package de.quantummaid.mapmaid.validatedtypeskotlin.types

import de.quantummaid.mapmaid.validatedtypeskotlin.validation.LongValidator

abstract class ValidatedLong private constructor(
    protected val safeValue: Long
) : ValueType<String> {
    constructor(validator: LongValidator, unsafe: String) : this(validator.validate(LongValidator.parseLong(unsafe)))

    override fun mappingValue(): String {
        return safeValue.toString()
    }

    override fun toString(): String {
        return safeValue.toString()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        if (this::class.java != other::class.java) return false

        other as ValidatedLong

        if (mappingValue() != other.mappingValue()) return false

        return true
    }

    override fun hashCode(): Int {
        return safeValue.hashCode()
    }

}
