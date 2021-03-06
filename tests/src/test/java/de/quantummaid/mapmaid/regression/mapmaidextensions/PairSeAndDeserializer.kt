package de.quantummaid.mapmaid.regression.mapmaidextensions

import de.quantummaid.mapmaid.builder.customtypes.CustomType
import de.quantummaid.mapmaid.debug.DebugInformation
import de.quantummaid.mapmaid.mapper.deserialization.DeserializerCallback
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.TypeDeserializer
import de.quantummaid.mapmaid.mapper.deserialization.validation.ExceptionTracker
import de.quantummaid.mapmaid.mapper.injector.Injector
import de.quantummaid.mapmaid.mapper.serialization.SerializationCallback
import de.quantummaid.mapmaid.mapper.serialization.serializers.TypeSerializer
import de.quantummaid.mapmaid.mapper.serialization.tracker.SerializationTracker
import de.quantummaid.mapmaid.mapper.universal.Universal
import de.quantummaid.mapmaid.mapper.universal.UniversalObject
import de.quantummaid.mapmaid.shared.mapping.CustomPrimitiveMappings
import de.quantummaid.reflectmaid.GenericType
import de.quantummaid.reflectmaid.ReflectMaid
import de.quantummaid.reflectmaid.typescanner.TypeIdentifier
import java.util.*

class PairSeAndDeserializer(
    private val first: GenericType<*>,
    private val second: GenericType<*>,
    private val typeIdentifierFirst: TypeIdentifier,
    private val typeIdentifierSecond: TypeIdentifier,
    private val reflectMaid: ReflectMaid
) : CustomType<Pair<*, *>> {

    companion object {
        fun pairSeAndDeserializer(first: GenericType<*>, second: GenericType<*>, reflectMaid: ReflectMaid): PairSeAndDeserializer {
            val typeIdentifierFirst = TypeIdentifier.typeIdentifierFor(reflectMaid.resolve(first))
            val typeIdentifierSecond = TypeIdentifier.typeIdentifierFor(reflectMaid.resolve(second))
            return PairSeAndDeserializer(first, second, typeIdentifierFirst, typeIdentifierSecond, reflectMaid)
        }
    }

    override fun type(): TypeIdentifier {
        val genericType: GenericType<*> = GenericType.genericType<Pair<*, *>>(
                Pair::class.java,
                listOf(first, second)
        )
        val resolvedType = reflectMaid.resolve(genericType)
        return TypeIdentifier.typeIdentifierFor(resolvedType)
    }

    override fun serializer(): Optional<TypeSerializer> {
        return Optional.of(object : TypeSerializer {

            override fun requiredTypes(): MutableList<TypeIdentifier> {
                return mutableListOf(
                        typeIdentifierFirst,
                        typeIdentifierSecond
                )
            }

            @Suppress("UNCHECKED_CAST")
            override fun serialize(
                    `object`: Any,
                    callback: SerializationCallback,
                    tracker: SerializationTracker,
                    customPrimitiveMappings: CustomPrimitiveMappings,
                    debugInformation: DebugInformation
            ): Universal {
                val (first, second) = `object` as Pair<Any, Any>
                val serializedFirst = callback.serializeDefinition(typeIdentifierFirst, first, tracker)
                val serializedSecond = callback.serializeDefinition(typeIdentifierSecond, second, tracker)
                return Universal.fromNativeJava(
                        mapOf(
                                "first" to serializedFirst.toNativeJava().toString(),
                                "second" to serializedSecond.toNativeJava().toString()
                        )
                )
            }

            override fun description(): String {
                return "Serializer for pair of type " +
                        "<${typeIdentifierFirst.description()}, ${typeIdentifierSecond.description()}>"
            }
        })
    }

    override fun deserializer(): Optional<TypeDeserializer> {
        return Optional.of(object : TypeDeserializer {
            override fun requiredTypes(): MutableList<TypeIdentifier> {
                return mutableListOf(typeIdentifierFirst, typeIdentifierSecond)
            }

            @Suppress("UNCHECKED_CAST")
            override fun <T : Any?> deserialize(
                    input: Universal,
                    exceptionTracker: ExceptionTracker,
                    injector: Injector,
                    callback: DeserializerCallback,
                    customPrimitiveMappings: CustomPrimitiveMappings,
                    typeIdentifier: TypeIdentifier,
                    debugInformation: DebugInformation
            ): T {
                val universalObject = TypeDeserializer.castSafely(
                        input,
                        UniversalObject::class.java,
                        exceptionTracker,
                        typeIdentifier,
                        debugInformation
                )
                val serializedFirst = universalObject.getField("first").orElseThrow()
                val first = callback.deserializeRecursive(
                        serializedFirst,
                        typeIdentifierFirst,
                        exceptionTracker,
                        injector,
                        debugInformation
                )
                val serializedSecond = universalObject.getField("second").orElseThrow()
                val second = callback.deserializeRecursive(
                        serializedSecond,
                        typeIdentifierSecond,
                        exceptionTracker,
                        injector,
                        debugInformation
                )
                return Pair(first, second) as T
            }

            override fun description(): String {
                return "Deserializer for pair of type " +
                        "<${typeIdentifierFirst.description()}, ${typeIdentifierSecond.description()}>"
            }
        })
    }
}
