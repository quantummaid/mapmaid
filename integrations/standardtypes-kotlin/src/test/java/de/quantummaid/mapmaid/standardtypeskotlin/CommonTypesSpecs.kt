package de.quantummaid.mapmaid.standardtypeskotlin

import de.quantummaid.mapmaid.MapMaid
import de.quantummaid.mapmaid.MapMaid.aMapMaid
import de.quantummaid.reflectmaid.GenericType
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test
import java.time.Instant

class CommonTypesSpecs {

    @Test
    fun commonTypesSpecs() {
        val mapMaid = aMapMaid()
            .withAllCommonTypesPreRegistered()
            .build()
        val serialized = mapMaid.serializeToUniversalObject(
            mapOf(
                "a" to 1,
                "b" to 1L,
                "c" to 1.0f,
                "d" to 10.0,
                "h" to Instant.ofEpochMilli(0)
            ),
            GenericType.genericType<Map<String, Any>>()
        )

        assertThat(serialized, `is`(mapOf(
            "a" to 1L,
            "b" to 1L,
            "c" to 1.0,
            "d" to 10.0,
            "h" to "1970-01-01T00:00:00Z"
        )))
    }
}