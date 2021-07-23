package de.quantummaid.mapmaid.standardtypeskotlin

import de.quantummaid.mapmaid.MapMaid.aMapMaid
import de.quantummaid.mapmaid.standardtypeskotlin.mixedcollections.withMixedCollectionSerializationSupport
import de.quantummaid.reflectmaid.GenericType.Companion.genericType
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test

class MixedCollectionSerializationSpecs {

    @Test
    fun simpleMapCanBeSerialized() {
        val mapMaid = aMapMaid()
            .withMixedCollectionSerializationSupport()
            .serializing(String::class.java)
            .build()

        val serialized = mapMaid.serializeToUniversalObject(mapOf("a" to "b"), genericType<Map<String, Any>>())
        assertThat(serialized, `is`(mapOf("a" to "b")))
    }

    @Test
    fun simpleListCanBeSerialized() {
        val mapMaid = aMapMaid()
            .withMixedCollectionSerializationSupport()
            .serializing(String::class.java)
            .build()

        val serialized = mapMaid.serializeToUniversalObject(listOf("a", "b", "c"), genericType<Collection<Any>>())
        assertThat(serialized, `is`(listOf("a", "b", "c")))
    }

    @Test
    fun simpleListCanBeSerializedAsList() {
        val mapMaid = aMapMaid()
            .withMixedCollectionSerializationSupport()
            .serializing(String::class.java)
            .build()

        val serialized = mapMaid.serializeToUniversalObject(listOf("a", "b", "c"), genericType<List<Any>>())
        assertThat(serialized, `is`(listOf("a", "b", "c")))
    }

    @Test
    fun simpleSetCanBeSerialized() {
        val mapMaid = aMapMaid()
            .withMixedCollectionSerializationSupport()
            .serializing(String::class.java)
            .build()

        val serialized = mapMaid.serializeToUniversalObject(setOf("a", "b", "c"), genericType<Collection<Any>>())
        assertThat(serialized, `is`(listOf("a", "b", "c")))
    }

    @Test
    fun simpleSetCanBeSerializedAsSet() {
        val mapMaid = aMapMaid()
            .withMixedCollectionSerializationSupport()
            .serializing(String::class.java)
            .build()

        val serialized = mapMaid.serializeToUniversalObject(setOf("a", "b", "c"), genericType<Set<Any>>())
        assertThat(serialized, `is`(listOf("a", "b", "c")))
    }

    @Test
    fun simpleMapCanBeSerializedWithMapInside() {
        val mapMaid = aMapMaid()
            .withMixedCollectionSerializationSupport()
            .serializing(String::class.java)
            .build()

        val serialized =
            mapMaid.serializeToUniversalObject(mapOf("a" to mapOf("b" to "c")), genericType<Map<String, Any>>())
        assertThat(serialized, `is`(mapOf("a" to mapOf("b" to "c"))))
    }

    @Test
    fun simpleMapCanBeSerializedWithListInside() {
        val mapMaid = aMapMaid()
            .withMixedCollectionSerializationSupport()
            .serializing(String::class.java)
            .build()

        val serialized =
            mapMaid.serializeToUniversalObject(mapOf("a" to listOf("a", "b", "c")), genericType<Map<String, Any>>())
        assertThat(serialized, `is`(mapOf("a" to listOf("a", "b", "c"))))
    }

    @Test
    fun simpleMapCanBeSerializedWithSetInside() {
        val mapMaid = aMapMaid()
            .withMixedCollectionSerializationSupport()
            .serializing(String::class.java)
            .build()

        val serialized =
            mapMaid.serializeToUniversalObject(mapOf("a" to setOf("a", "b", "c")), genericType<Map<String, Any>>())
        assertThat(serialized, `is`(mapOf("a" to listOf("a", "b", "c"))))
    }

    @Test
    fun simpleListCanBeSerializedWithMapInside() {
        val mapMaid = aMapMaid()
            .withMixedCollectionSerializationSupport()
            .serializing(String::class.java)
            .build()

        val serialized = mapMaid.serializeToUniversalObject(listOf(mapOf("a" to "b")), genericType<Collection<Any>>())
        assertThat(serialized, `is`(listOf(mapOf("a" to "b"))))
    }

    @Test
    fun simpleListCanBeSerializedWithListInside() {
        val mapMaid = aMapMaid()
            .withMixedCollectionSerializationSupport()
            .serializing(String::class.java)
            .build()

        val serialized =
            mapMaid.serializeToUniversalObject(listOf(listOf("a", "b", "c")), genericType<Collection<Any>>())
        assertThat(serialized, `is`(listOf(listOf("a", "b", "c"))))
    }

    @Test
    fun simpleListCanBeSerializedWithSetInside() {
        val mapMaid = aMapMaid()
            .withMixedCollectionSerializationSupport()
            .serializing(String::class.java)
            .build()

        val serialized =
            mapMaid.serializeToUniversalObject(listOf(setOf("a", "b", "c")), genericType<Collection<Any>>())
        assertThat(serialized, `is`(listOf(listOf("a", "b", "c"))))
    }

    @Test
    fun typeDeterminerCanBeCustomised() {
        class MyClass<X>(val value: X)

        val mapMaid = aMapMaid()
            .withMixedCollectionSerializationSupport {
                when (it) {
                    is MyClass<*> -> genericType<MyClass<String>>()
                    else -> null
                }
            }
            .serializing(String::class.java)
            .serializingCustomPrimitive(genericType<MyClass<String>>()) { it.value }
            .build()

        val serialized =
            mapMaid.serializeToUniversalObject(listOf(MyClass("foo")), genericType<Collection<Any>>())
        assertThat(serialized, `is`(listOf("foo")))
    }
}