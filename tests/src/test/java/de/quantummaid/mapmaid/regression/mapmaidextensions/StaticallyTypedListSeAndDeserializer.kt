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
import de.quantummaid.mapmaid.mapper.universal.UniversalCollection
import de.quantummaid.mapmaid.shared.identifier.TypeIdentifier
import de.quantummaid.mapmaid.shared.identifier.TypeIdentifier.typeIdentifierFor
import de.quantummaid.mapmaid.shared.mapping.CustomPrimitiveMappings
import de.quantummaid.reflectmaid.GenericType
import java.util.Optional

class StaticallyTypedListSeAndDeserializer(private val typeIdentifier: TypeIdentifier) : CustomType<List<*>> {

    companion object {
        fun staticallyTypedListSeAndDeserializer(clazz: Class<*>): StaticallyTypedListSeAndDeserializer {
            return StaticallyTypedListSeAndDeserializer(
                typeIdentifierFor(clazz),
            )
        }
    }

    override fun type(): TypeIdentifier {
        return typeIdentifierFor(
            GenericType.genericType<Any>(
                List::class.java,
                GenericType.fromResolvedType<Any>(typeIdentifier.realType),
            )
        )
    }

    override fun serializer(): Optional<TypeSerializer> {
        return Optional.of(object : TypeSerializer {
            override fun requiredTypes(): MutableList<TypeIdentifier> {
                return mutableListOf(typeIdentifier)
            }

            @Suppress("UNCHECKED_CAST")
            override fun serialize(
                `object`: Any,
                callback: SerializationCallback,
                tracker: SerializationTracker,
                customPrimitiveMappings: CustomPrimitiveMappings,
                debugInformation: DebugInformation
            ): Universal {
                val list = `object` as List<Any>
                val serializedList = list.map {
                    callback.serializeDefinition(typeIdentifier, it, tracker)
                }
                return UniversalCollection.universalCollection(serializedList)
            }

            override fun description(): String {
                return "Serializer for List<${typeIdentifier.description()}>"
            }
        })
    }

    override fun deserializer(): Optional<TypeDeserializer> {
        val innerTypeIdentifier = typeIdentifier
        return Optional.of(object : TypeDeserializer {
            override fun requiredTypes(): MutableList<TypeIdentifier> {
                return mutableListOf(typeIdentifier)
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
                val universalCollection = input as UniversalCollection
                val list = universalCollection.content()
                val serializedList = list.map {
                    callback.deserializeRecursive(it, innerTypeIdentifier, exceptionTracker, injector, debugInformation)
                }
                return serializedList as T
            }

            override fun description(): String {
                return "Deserializer for List<${typeIdentifier.description()}>"
            }
        })
    }
}
