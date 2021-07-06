package de.quantummaid.mapmaid.validatedtypeskotlin.types

import de.quantummaid.mapmaid.validatedtypeskotlin.validation.IntValidator


abstract class ValidatedInt private constructor(
    protected val safeValue: Int
) : ValueType<String> {
    constructor(validator: IntValidator, unsafe: String) : this(validator.validate(IntValidator.parseInt(unsafe)))

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

        other as ValidatedInt

        if (mappingValue() != other.mappingValue()) return false

        return true
    }

    override fun hashCode(): Int {
        return safeValue.hashCode()
    }

}
