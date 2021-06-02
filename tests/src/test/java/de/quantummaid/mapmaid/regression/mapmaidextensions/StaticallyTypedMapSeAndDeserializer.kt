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
import de.quantummaid.mapmaid.builder.resolving.framework.identifier.TypeIdentifier
import de.quantummaid.mapmaid.builder.resolving.framework.identifier.TypeIdentifier.typeIdentifierFor
import de.quantummaid.mapmaid.shared.mapping.CustomPrimitiveMappings
import de.quantummaid.reflectmaid.GenericType
import de.quantummaid.reflectmaid.ReflectMaid
import java.util.*

class StaticallyTypedMapSeAndDeserializer private constructor(
        private val key: GenericType<*>,
        private val value: GenericType<*>,
        private val typeIdentifierKey: TypeIdentifier,
        private val typeIdentifierValue: TypeIdentifier,
        private val reflectMaid: ReflectMaid
) : CustomType<Pair<*, *>> {

    companion object {
        fun staticallyTypedMapSeAndDeserializer(
                keyClass: Class<*>,
                valueClass: Class<*>,
                reflectMaid: ReflectMaid
        ): StaticallyTypedMapSeAndDeserializer {
            val key = GenericType.genericType(keyClass)
            val value = GenericType.genericType(valueClass)
            return staticallyTypedMapSeAndDeserializer(key, value, reflectMaid)
        }

        fun staticallyTypedMapSeAndDeserializer(
                key: GenericType<*>,
                value: GenericType<*>,
                reflectMaid: ReflectMaid
        ): StaticallyTypedMapSeAndDeserializer {
            return StaticallyTypedMapSeAndDeserializer(
                    key,
                    value,
                    typeIdentifierFor(reflectMaid.resolve(key)),
                    typeIdentifierFor(reflectMaid.resolve(value)),
                    reflectMaid)
        }
    }

    override fun type(): TypeIdentifier {
        return typeIdentifierFor(
                reflectMaid.resolve(GenericType.genericType<Any>(
                        Map::class.java,
                        listOf(key, value)
                ))
        )
    }

    override fun serializer(): Optional<TypeSerializer> {
        return Optional.of(object : TypeSerializer {
            override fun requiredTypes(): MutableList<TypeIdentifier> {
                return mutableListOf(typeIdentifierKey, typeIdentifierValue)
            }

            @Suppress("UNCHECKED_CAST")
            override fun serialize(
                    `object`: Any,
                    callback: SerializationCallback,
                    tracker: SerializationTracker,
                    customPrimitiveMappings: CustomPrimitiveMappings,
                    debugInformation: DebugInformation
            ): Universal {
                val map = `object` as Map<Any, Any>
                val serializedMap = map.entries
                        .map {
                            val universalKey = callback.serializeDefinition(typeIdentifierKey, it.key, tracker)
                            val universalValue = callback.serializeDefinition(typeIdentifierValue, it.value, tracker)
                            universalKey.toNativeJava().toString() to universalValue
                        }
                        .toMap()
                return UniversalObject.universalObject(serializedMap)
            }

            override fun description(): String {
                return "Serializer for Map" +
                        "<${typeIdentifierKey.description()}, ${typeIdentifierValue.description()}>"
            }
        })
    }

    override fun deserializer(): Optional<TypeDeserializer> {
        return Optional.of(object : TypeDeserializer {
            override fun requiredTypes(): MutableList<TypeIdentifier> {
                return mutableListOf(typeIdentifierKey, typeIdentifierValue)
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
                val universalObject = input as UniversalObject
                val serializedMap = universalObject.toNativeJava() as Map<String, Any>
                val map = serializedMap.entries
                        .map {
                            val keyUniversal = Universal.fromNativeJava(it.key)
                            val deserializedKey = callback.deserializeRecursive(
                                    keyUniversal,
                                    typeIdentifierKey,
                                    exceptionTracker,
                                    injector,
                                    debugInformation
                            )
                            val valueUniversal = Universal.fromNativeJava(it.value)
                            val deserializedValue = callback.deserializeRecursive(
                                    valueUniversal,
                                    typeIdentifierValue,
                                    exceptionTracker,
                                    injector,
                                    debugInformation
                            )
                            deserializedKey to deserializedValue
                        }
                        .toMap()
                return map as T
            }

            override fun description(): String {
                return "Deserializer for Map" +
                        "<${typeIdentifierKey.description()}, ${typeIdentifierValue.description()}>"
            }
        })
    }
}
