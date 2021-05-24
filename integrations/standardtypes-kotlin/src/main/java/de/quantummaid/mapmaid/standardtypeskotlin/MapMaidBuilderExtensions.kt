package de.quantummaid.mapmaid.standardtypeskotlin

import de.quantummaid.mapmaid.builder.MapMaidBuilder
import de.quantummaid.reflectmaid.GenericType

inline fun <reified T : Any> MapMaidBuilder.serializing(): MapMaidBuilder {
    val genericType = GenericType.genericType<T>()
    return serializing(genericType)
}

inline fun <reified T : Any> MapMaidBuilder.deserializing(): MapMaidBuilder {
    val genericType = GenericType.genericType<T>()
    return deserializing(genericType)
}

inline fun <reified T : Any> MapMaidBuilder.serializingAndDeserializing(): MapMaidBuilder {
    val genericType = GenericType.genericType<T>()
    return serializingAndDeserializing(genericType)
}
