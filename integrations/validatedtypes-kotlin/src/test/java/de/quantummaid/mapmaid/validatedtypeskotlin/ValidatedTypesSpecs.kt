package de.quantummaid.mapmaid.validatedtypeskotlin

import de.quantummaid.mapmaid.validatedtypeskotlin.types.*
import de.quantummaid.mapmaid.validatedtypeskotlin.validation.IntValidator.Companion.allOf
import de.quantummaid.mapmaid.validatedtypeskotlin.validation.IntValidator.Companion.max
import de.quantummaid.mapmaid.validatedtypeskotlin.validation.IntValidator.Companion.min
import de.quantummaid.mapmaid.validatedtypeskotlin.validation.LongValidator
import de.quantummaid.mapmaid.validatedtypeskotlin.validation.StringValidator.Companion.length
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class MyInt(value: String) : ValidatedInt(allOf(max(5), min(3)), value)
class MyLong(value: String) : ValidatedLong(LongValidator.allOf(LongValidator.max(5), LongValidator.min(3)), value)
class MyString(value: String) : ValidatedString(length(10, 20), value)
class MyUrl(value: String) : ValidatedUrl(value)
class MyEmailAddress(value: String) : ValidatedEmail(value)
class MyUuid(value: String) : ValidatedUuid(value)

class ValidatedTypesSpecs {

    @Test
    fun validatedInt() {
        val myInt = MyInt("4")
        assertNotNull(myInt)
        assertEquals("4", myInt.mappingValue())

        var exception: ValidationException? = null
        try {
            MyInt("10")
        } catch (e: ValidationException) {
            exception = e
        }
        assertNotNull(exception)
        assertEquals("must be smaller equal 5", exception!!.message)
    }

    @Test
    fun validatedLong() {
        val myLong = MyLong("4")
        assertNotNull(myLong)
        assertEquals("4", myLong.mappingValue())

        var exception: ValidationException? = null
        try {
            MyLong("fgwergwer")
        } catch (e: ValidationException) {
            exception = e
        }
        assertNotNull(exception)
        assertEquals("must be a valid integer number", exception!!.message)
    }

    @Test
    fun validatedString() {
        val myString = MyString("abcdefghij")
        assertNotNull(myString)
        assertEquals("abcdefghij", myString.mappingValue())

        var exception: ValidationException? = null
        try {
            MyString("abc")
        } catch (e: ValidationException) {
            exception = e
        }
        assertNotNull(exception)
        assertEquals("not enough characters(3), min 10 required", exception!!.message)
    }

    @Test
    fun validatedUrl() {
        val myUrl = MyUrl("https://google.com/")
        assertNotNull(myUrl)
        assertEquals("https://google.com/", myUrl.mappingValue())

        var exception: ValidationException? = null
        try {
            MyUrl("vtregweg")
        } catch (e: ValidationException) {
            exception = e
        }
        assertNotNull(exception)
        assertEquals("invalid url", exception!!.message)
    }

    @Test
    fun validatedEmail() {
        val myEmailAddress = MyEmailAddress("a@b.de")
        assertNotNull(myEmailAddress)
        assertEquals("a@b.de", myEmailAddress.mappingValue())

        var exception: ValidationException? = null
        try {
            MyEmailAddress("notanemail")
        } catch (e: ValidationException) {
            exception = e
        }
        assertNotNull(exception)
        assertEquals("invalid email address", exception!!.message)
    }

    @Test
    fun validatedUuid() {
        val myUuid = MyUuid("f99b952e-16e5-474d-9183-9228063bb9c8")
        assertNotNull(myUuid)
        assertEquals("f99b952e-16e5-474d-9183-9228063bb9c8", myUuid.mappingValue())

        var exception: ValidationException? = null
        try {
            MyUuid("f99b952e 16e5 474d 9183 9228063bb9c8")
        } catch (e: ValidationException) {
            exception = e
        }
        assertNotNull(exception)
        assertEquals("invalid uuid", exception!!.message)
    }
}