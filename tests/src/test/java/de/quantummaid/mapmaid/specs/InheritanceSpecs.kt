package de.quantummaid.mapmaid.specs

import de.quantummaid.mapmaid.MapMaid.aMapMaid
import de.quantummaid.mapmaid.debug.DebugInformation
import de.quantummaid.mapmaid.mapper.serialization.SerializationCallback
import de.quantummaid.mapmaid.mapper.serialization.serializers.TypeSerializer
import de.quantummaid.mapmaid.mapper.serialization.tracker.SerializationTracker
import de.quantummaid.mapmaid.mapper.universal.Universal
import de.quantummaid.mapmaid.mapper.universal.UniversalObject.universalObjectFromNativeMap
import de.quantummaid.mapmaid.shared.mapping.CustomPrimitiveMappings
import de.quantummaid.mapmaid.testsupport.givenwhenthen.Given.given
import de.quantummaid.reflectmaid.ReflectMaid
import de.quantummaid.reflectmaid.typescanner.TypeIdentifier
import de.quantummaid.reflectmaid.typescanner.TypeIdentifier.Companion.typeIdentifierFor
import org.junit.jupiter.api.Test

open class SuperClass(val field0: String, val field1: String)

class SubClass(
    field0: String,
    field1: String,
    val subField0: String,
    val subField1: String,
    val subField2: String
) : SuperClass(field0, field1)

class SubClassWithoutFields(field0: String, field1: String) : SuperClass(field0, field1)

class SubClassThatIsPotentiallyPrimitive(val value: String, field0: String, field1: String) : SuperClass(field0, field1)

class SuperClassSerializer(val reflectMaid: ReflectMaid) : TypeSerializer {
    override fun requiredTypes(): List<TypeIdentifier> {
        return listOf(reflectMaid.resolve<String>()).map { typeIdentifierFor(it) }
    }

    override fun serialize(
        `object`: Any?,
        callback: SerializationCallback?,
        tracker: SerializationTracker?,
        customPrimitiveMappings: CustomPrimitiveMappings?,
        debugInformation: DebugInformation?
    ): Universal {
        `object` as SuperClass
        return universalObjectFromNativeMap(
            mapOf(
                "field0" to `object`.field0,
                "field1" to `object`.field1
            )
        )
    }

    override fun description(): String {
        TODO("Not yet implemented")
    }
}

class InheritanceSpecs {

    @Test
    fun typeCanBeRegisteredForInheritance() {
        val reflectMaid = ReflectMaid.aReflectMaid()
        val resolvedSuperClass = reflectMaid.resolve<SuperClass>()
        val serializer = SuperClassSerializer(reflectMaid)
        given(
            aMapMaid(reflectMaid)
                .serializing(SubClass::class.java)
                .withAdvancedSettings { it.withSuperTypeSerializer(typeIdentifierFor(resolvedSuperClass), serializer) }
                .build()
        )
            .`when`().mapMaidSerializesToUniversalObject(SubClass("a", "b", "c", "d", "e"), SubClass::class.java)
            .theSerializationResultWas(
                mapOf(
                    "field0" to "a",
                    "field1" to "b",
                    "subField0" to "c",
                    "subField1" to "d",
                    "subField2" to "e"
                )
            )
    }

    @Test
    fun typeThatInheritsCanBeAutodetectedWithoutFields() {
        val reflectMaid = ReflectMaid.aReflectMaid()
        val resolvedSuperClass = reflectMaid.resolve<SuperClass>()
        val serializer = SuperClassSerializer(reflectMaid)
        given(
            aMapMaid(reflectMaid)
                .serializing(SubClassWithoutFields::class.java)
                .withAdvancedSettings { it.withSuperTypeSerializer(typeIdentifierFor(resolvedSuperClass), serializer) }
                .build()
        )
            .`when`()
            .mapMaidSerializesToUniversalObject(SubClassWithoutFields("a", "b"), SubClassWithoutFields::class.java)
            .theSerializationResultWas(
                mapOf(
                    "field0" to "a",
                    "field1" to "b"
                )
            )
    }

    @Test
    fun typeThatInheritsWillNotBeDetectedAsCustomPrimitive() {
        val reflectMaid = ReflectMaid.aReflectMaid()
        val resolvedSuperClass = reflectMaid.resolve<SuperClass>()
        val serializer = SuperClassSerializer(reflectMaid)
        given(
            aMapMaid(reflectMaid)
                .serializing(SubClassThatIsPotentiallyPrimitive::class.java)
                .withAdvancedSettings { it.withSuperTypeSerializer(typeIdentifierFor(resolvedSuperClass), serializer) }
                .build()
        )
            .`when`().mapMaidSerializesToUniversalObject(
                SubClassThatIsPotentiallyPrimitive("x", "a", "b"),
                SubClassThatIsPotentiallyPrimitive::class.java
            )
            .theSerializationResultWas(
                mapOf(
                    "value" to "x",
                    "field0" to "a",
                    "field1" to "b"
                )
            )
    }

    @Test
    fun typeThatInheritsCanBeCustomRegistered() {
        val reflectMaid = ReflectMaid.aReflectMaid()
        val resolvedSuperClass = reflectMaid.resolve<SuperClass>()
        val serializer = SuperClassSerializer(reflectMaid)
        given(
            aMapMaid(reflectMaid)
                .serializingCustomObject(SubClass::class.java) { builder ->
                    builder.withField("custom0", String::class.java) { it.subField0 }
                    builder.withField("custom1", String::class.java) { it.subField1 }
                    builder.withField("custom2", String::class.java) { it.subField2 }
                }
                .serializing(SubClass::class.java)
                .withAdvancedSettings { it.withSuperTypeSerializer(typeIdentifierFor(resolvedSuperClass), serializer) }
                .build()
        )
            .`when`().mapMaidSerializesToUniversalObject(
                SubClass("a", "b", "c", "d", "e"),
                SubClass::class.java
            )
            .theSerializationResultWas(
                mapOf(
                    "field0" to "a",
                    "field1" to "b",
                    "custom0" to "c",
                    "custom1" to "d",
                    "custom2" to "e"
                )
            )
    }
}