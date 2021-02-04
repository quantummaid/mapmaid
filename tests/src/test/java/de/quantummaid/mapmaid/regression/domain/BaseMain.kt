package de.quantummaid.mapmaid.regression.domain

import de.quantummaid.mapmaid.MapMaid
import de.quantummaid.mapmaid.builder.customtypes.DuplexType
import de.quantummaid.mapmaid.shared.identifier.TypeIdentifier
import de.quantummaid.mapmaid.regression.mapmaidextensions.ClassSeAndDeserializer
import de.quantummaid.mapmaid.regression.mapmaidextensions.PairSeAndDeserializer
import de.quantummaid.mapmaid.regression.mapmaidextensions.StaticallyTypedListSeAndDeserializer.Companion.staticallyTypedListSeAndDeserializer
import de.quantummaid.mapmaid.regression.mapmaidextensions.StaticallyTypedMapSeAndDeserializer.Companion.staticallyTypedMapSeAndDeserializer
import de.quantummaid.mapmaid.regression.mapmaidextensions.genericType

fun main() {
    val classes = listOf(
        MyInterface::class,
        MySealedClass::class,
        Impl3::class,
        Impl6::class,
    ).flatMap { it.sealedSubclasses }
        .map { it.java }
    val mapMaid = MapMaid.aMapMaid()
        .serializingAndDeserializingSubtypes(
            MyInterface::class.java,
            *classes.toTypedArray()
        )
        .serializingAndDeserializing(ClassSeAndDeserializer.classSeAndDeserializer())
        .serializingAndDeserializing(
            DuplexType.customPrimitive(
                Endpoint::class.java,
                { it.mappingValue() },
                { Endpoint.endpoint(it) } //
            )
        )
        .serializingAndDeserializing(
            staticallyTypedListSeAndDeserializer(
                SampleObject1::class.java,
            )
        )
        .serializingAndDeserializing(
            staticallyTypedMapSeAndDeserializer(
                TypeIdentifier.typeIdentifierFor(Name::class.java),
                TypeIdentifier.typeIdentifierFor(genericType<List<SampleObject2>>())
            )
        )
        .serializingAndDeserializing(
            staticallyTypedMapSeAndDeserializer(
                TypeIdentifier.typeIdentifierFor(Name::class.java),
                TypeIdentifier.typeIdentifierFor(genericType<List<SampleObject3>>())
            )
        )
        .serializingAndDeserializing(
            staticallyTypedMapSeAndDeserializer(
                String::class.java,
                String::class.java,
            )
        )
        .serializingAndDeserializingSubtypes(
            SampleInterface::class.java,
            SampleImpl1::class.java,
            SampleImpl2::class.java,
        )
        .serializingAndDeserializing(genericType<List<SampleObject3>>())
        .serializingAndDeserializing(
            PairSeAndDeserializer(
                TypeIdentifier.typeIdentifierFor(String::class.java),
                TypeIdentifier.typeIdentifierFor(Name::class.java)
            )
        )
        .build()

    val createCommand = Impl1(
        MessageId.newUnique(),
        TraceId("X"),
        ResourceId.newUnique(),
        Endpoint.endpoint("X/X"),
        listOf(
            SampleObject1(Name("Alfons"), Something("sth"))
        ),
        mapOf(
            "a" to "A",
            "b" to "B",
        )
    )
    val createCommandSerialized = mapMaid.serializeToJson(createCommand, MyInterface::class.java)
    val createCommand2 = mapMaid.deserializeJson(createCommandSerialized, MyInterface::class.java)
    ensureEquals(createCommand, createCommand2)

    val deleteCommand = Impl2(
        MessageId.newUnique(),
        TraceId("X"),
        ResourceId.newUnique(),
        Endpoint.endpoint("X/X"),
        SampleObject1(Name("Alfons"), Something("sth")),
        mapOf(
            Name("B") to listOf(
                SampleObject3(Something("sth1"), 1)
            ),
            Name("C") to listOf(
                SampleObject3(Something("sth2"), 2),
                SampleObject3(Something("sth3"), 3)
            ),
        )
    )
    val deleteCommandSerialized = mapMaid.serializeToJson(deleteCommand, MyInterface::class.java)
    val deleteCommand2 = mapMaid.deserializeJson(deleteCommandSerialized, MyInterface::class.java)
    ensureEquals(deleteCommand, deleteCommand2)

    val creationSucceeded = Impl4(
        MessageId.newUnique(),
        TraceId("X"),
        ResourceId.newUnique(),
        Endpoint.endpoint("X/X"),
        MessageId.newUnique(),
        mapOf(
            Name("B") to listOf(
                SampleObject2(Name("1"))
            ),
            Name("C") to listOf(
                SampleObject2(Name("2")),
                SampleObject2(Name("3"))
            ),
        ),
        listOf(
            SampleImpl1("A"),
            SampleImpl2("b"),
        )
    )
    val creationSucceededSerialized = mapMaid.serializeToJson(creationSucceeded, MyInterface::class.java)
    val creationSucceeded2 = mapMaid.deserializeJson(creationSucceededSerialized, MyInterface::class.java)
    ensureEquals(creationSucceeded, creationSucceeded2)

    val creationFailed = Impl5(
        MessageId.newUnique(),
        TraceId("X"),
        ResourceId.newUnique(),
        Endpoint.endpoint("X/X"),
        MessageId.newUnique(),
        listOf(
            SampleObject3(Something("A"), 1),
            SampleObject3(Something("B"), 2),
        ),
        Pair("A", Name("A")),
    )
    val creationFailedSerialized = mapMaid.serializeToJson(creationFailed, MyInterface::class.java)
    val creationFailed2 = mapMaid.deserializeJson(creationFailedSerialized, MyInterface::class.java)
    ensureEquals(creationFailed, creationFailed2)

    println("All Working")
}

private fun ensureEquals(o1: Any, o2: Any) {
    if (o1 != o2) {
        throw IllegalArgumentException("Not working")
    }
}
