package de.quantummaid.mapmaid.validatedtypeskotlin

import de.quantummaid.mapmaid.MapMaid
import de.quantummaid.mapmaid.MapMaid.aMapMaid
import de.quantummaid.mapmaid.debug.MapMaidException
import de.quantummaid.mapmaid.mapper.deserialization.validation.AggregatedValidationException
import de.quantummaid.mapmaid.mapper.deserialization.validation.UnexpectedExceptionThrownDuringDeserializationException
import de.quantummaid.mapmaid.validatedtypeskotlin.types.ValidatedInt
import de.quantummaid.mapmaid.validatedtypeskotlin.types.ValueType
import de.quantummaid.mapmaid.validatedtypeskotlin.validation.IntValidator
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class MapMaidValidatedTypeSpecs {

    @Test
    fun validatedEmailCanBeAutodetectedByMapMaid() {
        assertValidatedTypeCanBeRegistered(MyEmailAddress("a@b.de"), "a@b.de")
    }

    @Test
    fun validatedIntCanBeAutodetectedByMapMaid() {
        assertValidatedTypeCanBeRegistered(MyInt("4"), "4")
    }

    @Test
    fun validatedLongCanBeAutodetectedByMapMaid() {
        assertValidatedTypeCanBeRegistered(MyLong("4"), "4")
    }

    @Test
    fun validatedStringCanBeAutodetectedByMapMaid() {
        assertValidatedTypeCanBeRegistered(MyString("abcdefghij"), "abcdefghij")
    }

    @Test
    fun validatedUrlCanBeAutodetectedByMapMaid() {
        assertValidatedTypeCanBeRegistered(MyUrl("https://google.com/"), "https://google.com/")
    }

    @Test
    fun validatedUuidCanBeAutodetectedByMapMaid() {
        assertValidatedTypeCanBeRegistered(
            MyUuid("f99b952e-16e5-474d-9183-9228063bb9c8"),
            "f99b952e-16e5-474d-9183-9228063bb9c8"
        )
    }

    @Test
    fun supportForMapMaidValidatedTypesAutomaticallyRegistersValidationException() {
        val mapMaid = aMapMaid()
            .withSupportForMapMaidValidatedTypes()
            .serializingAndDeserializing(MyString::class.java)
            .build()

        var exception: AggregatedValidationException? = null
        try {
            mapMaid.deserializeFromUniversalObject("abc", MyString::class.java)
        } catch (e: AggregatedValidationException) {
            exception = e
        }
        assertEquals(
            "deserialization encountered validation errors. " +
                    "Validation error at '', not enough characters(3), min 10 required; ", exception!!.message
        )
    }

    @Test
    fun registrationOfValidationExceptionCanBeDisabled() {
        val mapMaid = aMapMaid()
            .withSupportForMapMaidValidatedTypes(registerValidationException = false)
            .serializingAndDeserializing(MyString::class.java)
            .build()

        var exception: UnexpectedExceptionThrownDuringDeserializationException? = null
        try {
            mapMaid.deserializeFromUniversalObject("abc", MyString::class.java)
        } catch (e: UnexpectedExceptionThrownDuringDeserializationException) {
            exception = e
        }
        assertEquals(
            "Unexpected exception thrown when deserializing field '': ValidationException",
            exception!!.message
        )
    }

    class MyIntWithPrivateConstructor private constructor(value: String) :
        ValidatedInt(IntValidator.allOf(IntValidator.max(5), IntValidator.min(3)), value)

    @Test
    fun undetectableValidatedTypeFailsWithAllErrorDetails() {
        var exception: MapMaidException? = null
        try {
            aMapMaid()
                .withSupportForMapMaidValidatedTypes()
                .serializingAndDeserializing(MyIntWithPrivateConstructor::class.java)
                .build()
        } catch (e: MapMaidException) {
            exception = e
        }
        assertTrue(exception!!.message!!.contains("unable to detect duplex"))
    }

    private inline fun <reified T : ValueType<String>> assertValidatedTypeCanBeRegistered(
        asObject: T,
        asString: String
    ) {
        val duplexMapMaid = aMapMaid()
            .withSupportForMapMaidValidatedTypes()
            .serializingAndDeserializing(T::class.java)
            .build()
        assertSerialization(duplexMapMaid, asObject, asString)
        assertDeserialization(duplexMapMaid, asObject, asString)

        val serializationMapMaid = aMapMaid()
            .withSupportForMapMaidValidatedTypes()
            .serializing(T::class.java)
            .build()
        assertSerialization(serializationMapMaid, asObject, asString)

        val deserializationMapMaid = aMapMaid()
            .withSupportForMapMaidValidatedTypes()
            .deserializing(T::class.java)
            .build()
        assertDeserialization(deserializationMapMaid, asObject, asString)
    }

    private inline fun <reified T : ValueType<String>> assertSerialization(
        mapMaid: MapMaid,
        asObject: T,
        asString: String
    ) {
        val serialized = mapMaid.serializeToUniversalObject(asObject, T::class.java)
        assertEquals(asString, serialized)
    }

    private inline fun <reified T : ValueType<String>> assertDeserialization(
        mapMaid: MapMaid,
        asObject: T,
        asString: String
    ) {
        val deserialized = mapMaid.deserializeFromUniversalObject(asString, T::class.java)
        assertEquals(asObject, deserialized)
        assertEquals(asString, deserialized.mappingValue())
    }
}
