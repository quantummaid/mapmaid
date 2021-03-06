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
import de.quantummaid.mapmaid.mapper.universal.UniversalString
import de.quantummaid.mapmaid.shared.mapping.CustomPrimitiveMappings
import de.quantummaid.reflectmaid.GenericType
import de.quantummaid.reflectmaid.ReflectMaid
import de.quantummaid.reflectmaid.typescanner.TypeIdentifier
import de.quantummaid.reflectmaid.typescanner.TypeIdentifier.Companion.typeIdentifierFor
import java.util.*

class ClassSeAndDeserializer(val reflectMaid: ReflectMaid) : CustomType<Class<*>> {

    override fun type(): TypeIdentifier {
        return typeIdentifierFor(reflectMaid.resolve(GenericType.genericType<Class<Any>>()))
    }

    override fun serializer(): Optional<TypeSerializer> {
        return Optional.of(object : TypeSerializer {
            override fun requiredTypes(): MutableList<TypeIdentifier> {
                return mutableListOf()
            }

            @Suppress("UNCHECKED_CAST")
            override fun serialize(
                `object`: Any,
                callback: SerializationCallback,
                tracker: SerializationTracker,
                customPrimitiveMappings: CustomPrimitiveMappings,
                debugInformation: DebugInformation
            ): Universal {
                val clazz = `object` as Class<Any>
                return UniversalString.universalString(clazz.name)
            }

            override fun description(): String {
                return "Serializer for Class<Any>"
            }
        })
    }

    override fun deserializer(): Optional<TypeDeserializer> {
        return Optional.of(object : TypeDeserializer {
            override fun requiredTypes(): MutableList<TypeIdentifier> {
                return mutableListOf()
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
                val universalString = input as UniversalString
                return Class.forName(universalString.toNativeStringValue()) as T
            }

            override fun description(): String {
                return "Deserializer for Class<Any>"
            }
        })
    }
}
