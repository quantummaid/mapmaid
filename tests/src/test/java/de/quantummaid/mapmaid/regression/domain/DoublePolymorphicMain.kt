package de.quantummaid.mapmaid.regression.domain

import de.quantummaid.mapmaid.MapMaid
import de.quantummaid.mapmaid.regression.mapmaidextensions.ClassSeAndDeserializer
import de.quantummaid.mapmaid.regression.mapmaidextensions.PairSeAndDeserializer
import de.quantummaid.mapmaid.regression.mapmaidextensions.StaticallyTypedListSeAndDeserializer.Companion.staticallyTypedListSeAndDeserializer
import de.quantummaid.mapmaid.regression.mapmaidextensions.StaticallyTypedMapSeAndDeserializer.Companion.staticallyTypedMapSeAndDeserializer
import de.quantummaid.reflectmaid.GenericType
import de.quantummaid.reflectmaid.ReflectMaid

interface OtherMessage

sealed class SealedOtherMessage(open val messageId: MessageId) : OtherMessage

data class OtherMessage1(override val messageId: MessageId, val createCommand: Impl1) : SealedOtherMessage(messageId)

data class OtherMessage2(override val messageId: MessageId, val creationSucceeded: Impl4) : SealedOtherMessage(messageId)

fun main() {
    val messageClasses = listOf(
            MyInterface::class,
            MySealedClass::class,
            Impl3::class,
            Impl6::class,
    ).flatMap { it.sealedSubclasses }
            .map { it.java }
    val otherMessageClasses = listOf(
            OtherMessage::class.java,
            OtherMessage1::class.java,
            OtherMessage2::class.java,
    )
    val reflectMaid = ReflectMaid.aReflectMaid()
    val mapMaid = MapMaid.aMapMaid(reflectMaid)
            .serializingAndDeserializingSubtypes(
                    MyInterface::class.java,
                    *messageClasses.toTypedArray()
            )
            .serializingAndDeserializingSubtypes(
                    OtherMessage::class.java,
                    *otherMessageClasses.toTypedArray()
            )
            .serializingAndDeserializing(ClassSeAndDeserializer(reflectMaid))
            .serializingAndDeserializingCustomPrimitive(
                    Endpoint::class.java,
                    { it.mappingValue() },
                    { Endpoint.endpoint(it) } //
            )
            .serializingAndDeserializing(
                    staticallyTypedListSeAndDeserializer(
                            SampleObject1::class.java,
                            reflectMaid
                    )
            )
            .serializingAndDeserializing(
                    staticallyTypedMapSeAndDeserializer(
                            GenericType.genericType(Name::class.java),
                            GenericType.genericType<List<SampleObject2>>(),
                            reflectMaid
                    )
            )
            .serializingAndDeserializing(
                    staticallyTypedMapSeAndDeserializer(
                            GenericType.genericType(Name::class.java),
                            GenericType.genericType<List<SampleObject3>>(),
                            reflectMaid
                    )
            )
            .serializingAndDeserializing(
                    staticallyTypedMapSeAndDeserializer(
                            GenericType.genericType(String::class),
                            GenericType.genericType(String::class),
                            reflectMaid
                    )
            )
            .serializingAndDeserializingSubtypes(
                    SampleInterface::class.java,
                    SampleImpl1::class.java,
                    SampleImpl2::class.java,
            )
            .serializingAndDeserializing(GenericType.genericType<List<SampleObject3>>())
            .serializingAndDeserializing(
                    PairSeAndDeserializer.pairSeAndDeserializer(
                            GenericType.genericType(String::class),
                            GenericType.genericType(Name::class),
                            reflectMaid
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
    val otherMessage1 = OtherMessage1(MessageId.newUnique(), createCommand)
    val otherMessage1Serialized = mapMaid.serializeToJson(otherMessage1, OtherMessage::class.java)
    val otherMessage12 = mapMaid.deserializeJson(otherMessage1Serialized, OtherMessage::class.java)
    ensureEquals(otherMessage1, otherMessage12)

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
                    SampleImpl1("123"),
                    SampleImpl2("456"),
            )
    )
    val otherMessage2 = OtherMessage2(MessageId.newUnique(), creationSucceeded)
    val otherMessage2Serialized = mapMaid.serializeToJson(otherMessage2, OtherMessage::class.java)
    val otherMessage22 = mapMaid.deserializeJson(otherMessage2Serialized, OtherMessage::class.java)
    ensureEquals(otherMessage2, otherMessage22)
    println("All Working")
}

private fun ensureEquals(o1: Any, o2: Any) {
    if (o1 != o2) {
        throw IllegalArgumentException("Not working")
    }
}
