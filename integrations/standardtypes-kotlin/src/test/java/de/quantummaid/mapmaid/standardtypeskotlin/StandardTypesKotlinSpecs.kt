package de.quantummaid.mapmaid.standardtypeskotlin

import de.quantummaid.mapmaid.MapMaid.aMapMaid
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.Instant

class StandardTypesKotlinSpecs {

    @Test
    fun pairCanBeSerialized() {
        val mapMaid = aMapMaid()
            .withSupportForStandardKotlinTypes()
            .serializing<Pair<String, Int>>()
            .build()
        val universalObject = mapMaid.serializeToUniversalObject(Pair("abc", 42))
        assertThat(
            universalObject, `is`(
                mapOf(
                    "first" to "abc",
                    "second" to "42"
                )
            )
        )
    }

    @Test
    fun pairCanBeDeserialized() {
        val mapMaid = aMapMaid()
            .withSupportForStandardKotlinTypes()
            .deserializing<Pair<String, Int>>()
            .build()
        val pair = mapMaid.deserializeFromUniversalObject<Pair<String, Int>>(
            mapOf(
                "first" to "abc",
                "second" to 42L
            )
        )
        assertThat(pair, `is`(Pair("abc", 42)))
    }

    @Test
    fun pairCanBeSerializedAndDeserialized() {
        val mapMaid = aMapMaid()
            .withSupportForStandardKotlinTypes()
            .serializingAndDeserializing<Pair<String, Int>>()
            .build()
        val universalObject = mapMaid.serializeToUniversalObject(Pair("abc", 42))
        assertThat(
            universalObject, `is`(
                mapOf(
                    "first" to "abc",
                    "second" to "42"
                )
            )
        )
        val pair = mapMaid.deserializeFromUniversalObject<Pair<String, Int>>(
            mapOf(
                "first" to "abc",
                "second" to 42L
            )
        )
        assertThat(pair, `is`(Pair("abc", 42)))
    }

    @Test
    fun instantCanBeSerializedAndDeserialized() {
        val mapMaid = aMapMaid()
            .withSupportForStandardKotlinTypes(true)
            .build()
        val universalObject = mapMaid.serializeToUniversalObject(Instant.ofEpochMilli(0))
        assertThat(universalObject, `is`("1970-01-01T00:00:00Z"))

        val instant = mapMaid.deserializeFromUniversalObject<Instant>("1970-01-01T00:00:00Z")
        assertThat(instant, `is`(Instant.ofEpochMilli(0)))
    }

    @Test
    fun durationCanBeSerializedAndDeserialized() {
        val duration = Duration.between(Instant.ofEpochMilli(0), Instant.ofEpochMilli(1000))
        val mapMaid = aMapMaid()
            .withSupportForStandardKotlinTypes(true)
            .build()
        val universalObject = mapMaid.serializeToUniversalObject(duration)
        assertThat(universalObject, `is`("PT1S"))

        val deserialized = mapMaid.deserializeFromUniversalObject<Duration>("PT1S")
        assertThat(deserialized, `is`(duration))
    }
}