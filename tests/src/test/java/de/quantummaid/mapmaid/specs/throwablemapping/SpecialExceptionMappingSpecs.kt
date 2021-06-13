package de.quantummaid.mapmaid.specs.throwablemapping

import de.quantummaid.mapmaid.MapMaid.aMapMaid
import de.quantummaid.mapmaid.testsupport.givenwhenthen.Given.given
import de.quantummaid.mapmaid.testsupport.givenwhenthen.structurevalidation.validators.FixedValidator.fixed
import de.quantummaid.mapmaid.testsupport.givenwhenthen.structurevalidation.validators.ListValidator.listOf
import de.quantummaid.mapmaid.testsupport.givenwhenthen.structurevalidation.validators.MapValidator.map
import de.quantummaid.mapmaid.testsupport.givenwhenthen.structurevalidation.validators.NullValidator.nullValue
import de.quantummaid.mapmaid.testsupport.givenwhenthen.structurevalidation.validators.StringValidator.string
import org.junit.jupiter.api.Test

class MySpecialException : RuntimeException("foo")

class MyDtoExceptionWithoutMessage : RuntimeException()

class MyDtoException(val field0: String, val field1: String, val field2: String) : RuntimeException("foo")

class MyPotentialPrimitiveException(val field: String) : RuntimeException("foo")

class UserWithEmailAlreadyExistsException(
    val request: String,
    val existingUsername: String,
    val existingUserEmail: String,
    override val message: String,
    override val cause: Throwable
) : Exception()

class SpecialExceptionMappingSpecs {

    @Test
    fun specialExceptionWithoutFieldsCanBeSerialized() {
        val exception = MySpecialException()
        given(
            aMapMaid()
                .serializing(MySpecialException::class.java)
                .build()
        )
            .`when`().mapMaidSerializesToUniversalObject(exception, MySpecialException::class.java)
            .theSerializationResultMatches(
                map()
                    .key("message", fixed("foo"))
                    .key("type", fixed("de.quantummaid.mapmaid.specs.throwablemapping.MySpecialException"))
                    .key("frames", listOf(string()))
            )
    }

    @Test
    fun specialExceptionWithoutMessageCanBeSerialized() {
        val exception = MyDtoExceptionWithoutMessage()
        given(
            aMapMaid()
                .serializing(MyDtoExceptionWithoutMessage::class.java)
                .build()
        )
            .`when`().mapMaidSerializesToUniversalObject(exception, MyDtoExceptionWithoutMessage::class.java)
            .theSerializationResultMatches(
                map()
                    .key("message", nullValue())
                    .key("type", fixed("de.quantummaid.mapmaid.specs.throwablemapping.MyDtoExceptionWithoutMessage"))
                    .key("frames", listOf(string()))
            )
    }

    @Test
    fun specialExceptionIsSerializedWithFields() {
        val exception = MyDtoException("a", "b", "c")
        given(
            aMapMaid()
                .serializing(MyDtoException::class.java)
                .build()
        )
            .`when`().mapMaidSerializesToUniversalObject(exception, MyDtoException::class.java)
            .theSerializationResultMatches(
                map()
                    .key("field0", fixed("a"))
                    .key("field1", fixed("b"))
                    .key("field2", fixed("c"))
                    .key("message", fixed("foo"))
                    .key("type", fixed("de.quantummaid.mapmaid.specs.throwablemapping.MyDtoException"))
                    .key("frames", listOf(string()))
            )
    }

    @Test
    fun specialExceptionsWillNotBecomePrimitives() {
        val exception = MyPotentialPrimitiveException("a")
        given(
            aMapMaid()
                .serializing(MyPotentialPrimitiveException::class.java)
                .build()
        )
            .`when`().mapMaidSerializesToUniversalObject(exception, MyPotentialPrimitiveException::class.java)
            .theSerializationResultMatches(
                map()
                    .key("field", fixed("a"))
                    .key("message", fixed("foo"))
                    .key("type", fixed("de.quantummaid.mapmaid.specs.throwablemapping.MyPotentialPrimitiveException"))
                    .key("frames", listOf(string()))
            )
    }

    @Test
    fun specialExceptionCanHaveCause() {
        val exception = MyDtoException("a", "b", "c")
        exception.initCause(MySpecialException())
        given(
            aMapMaid()
                .serializing(MyDtoException::class.java)
                .build()
        )
            .`when`().mapMaidSerializesToUniversalObject(exception, MyDtoException::class.java)
            .theSerializationResultMatches(
                map()
                    .key("field0", fixed("a"))
                    .key("field1", fixed("b"))
                    .key("field2", fixed("c"))
                    .key("message", fixed("foo"))
                    .key("type", fixed("de.quantummaid.mapmaid.specs.throwablemapping.MyDtoException"))
                    .key("frames", listOf(string()))
                    .key(
                        "cause", map()
                            .key("message", fixed("foo"))
                            .key("type", fixed("de.quantummaid.mapmaid.specs.throwablemapping.MySpecialException"))
                            .key("frames", listOf(string()))
                    )
            )
    }


    @Test
    fun causeOfSpecialExceptionThatIsItselfRegisteredSpecialExceptionIsSerializedWithoutItsFields() {
        val exception = MyDtoException("a", "b", "c")
        exception.initCause(MyPotentialPrimitiveException("x"))
        given(
            aMapMaid()
                .serializing(MyDtoException::class.java)
                .serializing(MyPotentialPrimitiveException::class.java)
                .build()
        )
            .`when`().mapMaidSerializesToUniversalObject(exception, MyDtoException::class.java)
            .theSerializationResultMatches(
                map()
                    .key("field0", fixed("a"))
                    .key("field1", fixed("b"))
                    .key("field2", fixed("c"))
                    .key("message", fixed("foo"))
                    .key("type", fixed("de.quantummaid.mapmaid.specs.throwablemapping.MyDtoException"))
                    .key("frames", listOf(string()))
                    .key(
                        "cause", map()
                            .key("message", fixed("foo"))
                            .key(
                                "type",
                                fixed("de.quantummaid.mapmaid.specs.throwablemapping.MyPotentialPrimitiveException")
                            )
                            .key("frames", listOf(string()))
                    )
            )
    }

    @Test
    fun specialExceptionThatOverwritesMessageAndCause() {
        val exception = UserWithEmailAlreadyExistsException("a", "b", "c", "foo", UnsupportedOperationException())
        given(
            aMapMaid()
                .serializing(UserWithEmailAlreadyExistsException::class.java)
                .build()
        )
            .`when`().mapMaidSerializesToUniversalObject(exception, UserWithEmailAlreadyExistsException::class.java)
            .theSerializationResultMatches(
                map()
                    .key("request", fixed("a"))
                    .key("existingUsername", fixed("b"))
                    .key("existingUserEmail", fixed("c"))
                    .key("message", fixed("foo"))
                    .key(
                        "type",
                        fixed("de.quantummaid.mapmaid.specs.throwablemapping.UserWithEmailAlreadyExistsException")
                    )
                    .key("frames", listOf(string()))
                    .key(
                        "cause", map()
                            .key("message", nullValue())
                            .key("type", fixed("java.lang.UnsupportedOperationException"))
                            .key("frames", listOf(string()))
                    )
            )
    }

    @Test
    fun exceptionFromJavaLangCanBeRegistered() {
        val exception = UnsupportedOperationException()
        given(
            aMapMaid()
                .serializing(UnsupportedOperationException::class.java)
                .build()
        )
            .`when`().mapMaidSerializesToUniversalObject(exception, UnsupportedOperationException::class.java)
            .theSerializationResultMatches(
                map()
                    .key("message", nullValue())
                    .key("type", fixed("java.lang.UnsupportedOperationException"))
                    .key("frames", listOf(string()))
            )
    }

    @Test
    fun throwableSupportCanBeDisabled() {
        val exception = MyDtoException("a", "b", "c")
        given(
            aMapMaid()
                .serializing(MyDtoException::class.java)
                .withAdvancedSettings { it.doNotRegisterThrowableSerializationSupport() }
                .build()
        )
            .`when`().mapMaidSerializesToUniversalObject(exception, MyDtoException::class.java)
            .theSerializationResultMatches(
                map()
                    .key("field0", fixed("a"))
                    .key("field1", fixed("b"))
                    .key("field2", fixed("c"))
            )
    }

    @Test
    fun specialExceptionThatOverwritesMessageAndCauseWorksIfThrowableSupportIsDisabled() {
        val exception = UserWithEmailAlreadyExistsException("a", "b", "c", "foo", UnsupportedOperationException())
        given(
            aMapMaid()
                .serializing(UserWithEmailAlreadyExistsException::class.java)
                .withAdvancedSettings { it.doNotRegisterThrowableSerializationSupport() }
                .build()
        )
            .`when`().mapMaidSerializesToUniversalObject(exception, UserWithEmailAlreadyExistsException::class.java)
            .theSerializationResultMatches(
                map()
                    .key("request", fixed("a"))
                    .key("existingUsername", fixed("b"))
                    .key("existingUserEmail", fixed("c"))
            )
    }

    @Test
    fun specialExceptionCanBeCustomRegistered() {
        val exception = MyDtoException("a", "b", "c")
        given(
            aMapMaid()
                .serializingCustomObject(MyDtoException::class.java) { builder ->
                    builder.withField("custom0", String::class.java) { it.field0 }
                    builder.withField("custom1", String::class.java) { it.field1 }
                    builder.withField("custom2", String::class.java) { it.field2 }
                }
                .serializing(MyDtoException::class.java)
                .build()
        )
            .`when`().mapMaidSerializesToUniversalObject(exception, MyDtoException::class.java)
            .theSerializationResultMatches(
                map()
                    .key("custom0", fixed("a"))
                    .key("custom1", fixed("b"))
                    .key("custom2", fixed("c"))
                    .key("message", fixed("foo"))
                    .key("type", fixed("de.quantummaid.mapmaid.specs.throwablemapping.MyDtoException"))
                    .key("frames", listOf(string()))
            )
    }
}