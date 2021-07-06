package de.quantummaid.mapmaid.validatedtypeskotlin.types

interface ValueType<O> {
    fun mappingValue(): O
    override fun toString(): String
    override fun equals(other: Any?): Boolean
    override fun hashCode(): Int
}
