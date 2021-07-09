package de.quantummaid.mapmaid.specs

import de.quantummaid.mapmaid.MapMaid.aMapMaid
import de.quantummaid.mapmaid.domain.AComplexType
import de.quantummaid.mapmaid.domain.AComplexType.deserialize
import de.quantummaid.mapmaid.domain.ANumber.fromInt
import de.quantummaid.mapmaid.domain.AString
import de.quantummaid.mapmaid.domain.AString.fromStringValue
import de.quantummaid.mapmaid.testsupport.givenwhenthen.Given.given
import de.quantummaid.reflectmaid.GenericType.Companion.genericType
import org.junit.jupiter.api.Test

class MapSpecs {

    @Test
    fun mapCanBeSerialized() {
        given(
            aMapMaid()
                .serializing(genericType<Map<AString, AComplexType>>())
                .build()
        )
            .`when`().mapMaidSerializesToUniversalObject(
                mapOf(
                    fromStringValue("key0") to deserialize(
                        fromStringValue("foo0"),
                        fromStringValue("bar0"),
                        fromInt(42),
                        fromInt(1337),
                    ),
                    fromStringValue("key1") to deserialize(
                        fromStringValue("foo1"),
                        fromStringValue("bar1"),
                        fromInt(43),
                        fromInt(1338),
                    ),
                    fromStringValue("key2") to deserialize(
                        fromStringValue("foo2"),
                        fromStringValue("bar2"),
                        fromInt(44),
                        fromInt(1339),
                    )
                ),
                genericType<Map<AString, AComplexType>>()
            )
            .theSerializationResultWas(
                mapOf(
                    "key0" to mapOf(
                        "stringA" to "foo0",
                        "stringB" to "bar0",
                        "number1" to "42",
                        "number2" to "1337",
                    ),
                    "key1" to mapOf(
                        "stringA" to "foo1",
                        "stringB" to "bar1",
                        "number1" to "43",
                        "number2" to "1338",
                    ),
                    "key2" to mapOf(
                        "stringA" to "foo2",
                        "stringB" to "bar2",
                        "number1" to "44",
                        "number2" to "1339",
                    ),
                )
            )
    }

    @Test
    fun mapCanBeDeserialized() {
        given(
            aMapMaid()
                .deserializing(genericType<Map<AString, AComplexType>>())
                .build()
        )
            .`when`().mapMaidDeserializesTheMap(
                mapOf(
                    "key0" to mapOf(
                        "stringA" to "foo0",
                        "stringB" to "bar0",
                        "number1" to "42",
                        "number2" to "13",
                    ),
                    "key1" to mapOf(
                        "stringA" to "foo1",
                        "stringB" to "bar1",
                        "number1" to "43",
                        "number2" to "13",
                    ),
                    "key2" to mapOf(
                        "stringA" to "foo2",
                        "stringB" to "bar2",
                        "number1" to "44",
                        "number2" to "13",
                    ),
                )
            ).toTheType(genericType<Map<AString, AComplexType>>())
            .noExceptionHasBeenThrown()
            .theDeserializedObjectIs(
                mapOf(
                    fromStringValue("key0") to deserialize(
                        fromStringValue("foo0"),
                        fromStringValue("bar0"),
                        fromInt(42),
                        fromInt(13),
                    ),
                    fromStringValue("key1") to deserialize(
                        fromStringValue("foo1"),
                        fromStringValue("bar1"),
                        fromInt(43),
                        fromInt(13),
                    ),
                    fromStringValue("key2") to deserialize(
                        fromStringValue("foo2"),
                        fromStringValue("bar2"),
                        fromInt(44),
                        fromInt(13),
                    )
                )
            )
    }

    @Test
    fun mutableMapCanBeSerialized() {
        given(
            aMapMaid()
                .serializing(genericType<MutableMap<AString, AComplexType>>())
                .build()
        )
            .`when`().mapMaidSerializesToUniversalObject(
                mapOf(
                    fromStringValue("key0") to deserialize(
                        fromStringValue("foo0"),
                        fromStringValue("bar0"),
                        fromInt(42),
                        fromInt(1337),
                    ),
                    fromStringValue("key1") to deserialize(
                        fromStringValue("foo1"),
                        fromStringValue("bar1"),
                        fromInt(43),
                        fromInt(1338),
                    ),
                    fromStringValue("key2") to deserialize(
                        fromStringValue("foo2"),
                        fromStringValue("bar2"),
                        fromInt(44),
                        fromInt(1339),
                    )
                ),
                genericType<MutableMap<AString, AComplexType>>()
            )
            .theSerializationResultWas(
                mapOf(
                    "key0" to mapOf(
                        "stringA" to "foo0",
                        "stringB" to "bar0",
                        "number1" to "42",
                        "number2" to "1337",
                    ),
                    "key1" to mapOf(
                        "stringA" to "foo1",
                        "stringB" to "bar1",
                        "number1" to "43",
                        "number2" to "1338",
                    ),
                    "key2" to mapOf(
                        "stringA" to "foo2",
                        "stringB" to "bar2",
                        "number1" to "44",
                        "number2" to "1339",
                    ),
                )
            )
    }

    @Test
    fun mutableMapCanBeDeserialized() {
        given(
            aMapMaid()
                .deserializing(genericType<MutableMap<AString, AComplexType>>())
                .build()
        )
            .`when`().mapMaidDeserializesTheMap(
                mapOf(
                    "key0" to mapOf(
                        "stringA" to "foo0",
                        "stringB" to "bar0",
                        "number1" to "42",
                        "number2" to "13",
                    ),
                    "key1" to mapOf(
                        "stringA" to "foo1",
                        "stringB" to "bar1",
                        "number1" to "43",
                        "number2" to "13",
                    ),
                    "key2" to mapOf(
                        "stringA" to "foo2",
                        "stringB" to "bar2",
                        "number1" to "44",
                        "number2" to "13",
                    ),
                )
            ).toTheType(genericType<MutableMap<AString, AComplexType>>())
            .noExceptionHasBeenThrown()
            .theDeserializedObjectIs(
                mapOf(
                    fromStringValue("key0") to deserialize(
                        fromStringValue("foo0"),
                        fromStringValue("bar0"),
                        fromInt(42),
                        fromInt(13),
                    ),
                    fromStringValue("key1") to deserialize(
                        fromStringValue("foo1"),
                        fromStringValue("bar1"),
                        fromInt(43),
                        fromInt(13),
                    ),
                    fromStringValue("key2") to deserialize(
                        fromStringValue("foo2"),
                        fromStringValue("bar2"),
                        fromInt(44),
                        fromInt(13),
                    )
                )
            )
    }

    @Test
    fun mutableMapCanBeSerializedAsMap() {
        given(
            aMapMaid()
                .serializing(genericType<MutableMap<AString, AComplexType>>())
                .build()
        )
            .`when`().mapMaidSerializesToUniversalObject(
                mapOf(
                    fromStringValue("key0") to deserialize(
                        fromStringValue("foo0"),
                        fromStringValue("bar0"),
                        fromInt(42),
                        fromInt(1337),
                    ),
                    fromStringValue("key1") to deserialize(
                        fromStringValue("foo1"),
                        fromStringValue("bar1"),
                        fromInt(43),
                        fromInt(1338),
                    ),
                    fromStringValue("key2") to deserialize(
                        fromStringValue("foo2"),
                        fromStringValue("bar2"),
                        fromInt(44),
                        fromInt(1339),
                    )
                ),
                genericType<Map<AString, AComplexType>>()
            )
            .theSerializationResultWas(
                mapOf(
                    "key0" to mapOf(
                        "stringA" to "foo0",
                        "stringB" to "bar0",
                        "number1" to "42",
                        "number2" to "1337",
                    ),
                    "key1" to mapOf(
                        "stringA" to "foo1",
                        "stringB" to "bar1",
                        "number1" to "43",
                        "number2" to "1338",
                    ),
                    "key2" to mapOf(
                        "stringA" to "foo2",
                        "stringB" to "bar2",
                        "number1" to "44",
                        "number2" to "1339",
                    ),
                )
            )
    }

    @Test
    fun mutableMapCanBeDeserializedAsMap() {
        given(
            aMapMaid()
                .deserializing(genericType<MutableMap<AString, AComplexType>>())
                .build()
        )
            .`when`().mapMaidDeserializesTheMap(
                mapOf(
                    "key0" to mapOf(
                        "stringA" to "foo0",
                        "stringB" to "bar0",
                        "number1" to "42",
                        "number2" to "13",
                    ),
                    "key1" to mapOf(
                        "stringA" to "foo1",
                        "stringB" to "bar1",
                        "number1" to "43",
                        "number2" to "13",
                    ),
                    "key2" to mapOf(
                        "stringA" to "foo2",
                        "stringB" to "bar2",
                        "number1" to "44",
                        "number2" to "13",
                    ),
                )
            ).toTheType(genericType<Map<AString, AComplexType>>())
            .noExceptionHasBeenThrown()
            .theDeserializedObjectIs(
                mapOf(
                    fromStringValue("key0") to deserialize(
                        fromStringValue("foo0"),
                        fromStringValue("bar0"),
                        fromInt(42),
                        fromInt(13),
                    ),
                    fromStringValue("key1") to deserialize(
                        fromStringValue("foo1"),
                        fromStringValue("bar1"),
                        fromInt(43),
                        fromInt(13),
                    ),
                    fromStringValue("key2") to deserialize(
                        fromStringValue("foo2"),
                        fromStringValue("bar2"),
                        fromInt(44),
                        fromInt(13),
                    )
                )
            )
    }
}