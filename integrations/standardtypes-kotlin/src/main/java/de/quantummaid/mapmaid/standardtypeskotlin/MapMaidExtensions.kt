package de.quantummaid.mapmaid.standardtypeskotlin

import de.quantummaid.mapmaid.MapMaid
import de.quantummaid.reflectmaid.GenericType

inline fun <reified T : Any> MapMaid.serializeToJson(obj: T): String {
    val genericType = GenericType.genericType<T>()
    return serializeToJson(obj, genericType)
}

inline fun <reified T : Any> MapMaid.serializeToYaml(obj: T): String {
    val genericType = GenericType.genericType<T>()
    return serializeToYaml(obj, genericType)
}

inline fun <reified T : Any> MapMaid.serializeToXml(obj: T): String {
    val genericType = GenericType.genericType<T>()
    return serializeToXml(obj, genericType)
}

inline fun <reified T : Any> MapMaid.serializeToUniversalObject(obj: T): Any? {
    val genericType = GenericType.genericType<T>()
    return serializeToUniversalObject(obj, genericType)
}

inline fun <reified T: Any> MapMaid.deserializeJson(json: String): T {
    val genericType = GenericType.genericType<T>()
    return this.deserializeJson(json, genericType)
}

inline fun <reified T: Any> MapMaid.deserializeYaml(json: String): T {
    val genericType = GenericType.genericType<T>()
    return this.deserializeYaml(json, genericType)
}

inline fun <reified T: Any> MapMaid.deserializeXml(json: String): T {
    val genericType = GenericType.genericType<T>()
    return this.deserializeXml(json, genericType)
}

inline fun <reified T: Any> MapMaid.deserializeFromUniversalObject(obj: Any): T {
    val genericType = GenericType.genericType<T>()
    return deserializeFromUniversalObject(obj, genericType)
}