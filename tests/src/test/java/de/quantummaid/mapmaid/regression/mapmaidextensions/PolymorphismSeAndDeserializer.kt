package test.mapmaidextensions

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
import de.quantummaid.mapmaid.shared.identifier.TypeIdentifier
import de.quantummaid.mapmaid.shared.mapping.CustomPrimitiveMappings
import de.quantummaid.reflectmaid.ReflectMaid
import java.util.*

open class PolymorphisticCustomMapMaidType<T>(
        private val typeIdentifier: TypeIdentifier,
        val subClasses: List<Class<out T>>,
        val reflectMaid: ReflectMaid,
        additionalRequiredTypes: List<Class<*>> = emptyList()
) : CustomType<T> {
    private val typeToClass = subClasses
            .map { it.simpleName to it }
            .toMap()
    private val classToType = subClasses
            .map { it to it.simpleName }
            .toMap()
    private val requiredTypes = subClasses
            .map { reflectMaid.resolve(it) }
            .map { TypeIdentifier.typeIdentifierFor(it) }
            .toMutableList()

    init {
        additionalRequiredTypes
                .map { reflectMaid.resolve(it) }
                .map { TypeIdentifier.typeIdentifierFor(it) }
                .forEach { requiredTypes.add(it) }
    }

    override fun deserializer(): Optional<TypeDeserializer> {
        return Optional.of(PolymorphisticDeserializer(typeToClass, requiredTypes, reflectMaid))
    }

    override fun type(): TypeIdentifier {
        return typeIdentifier
    }

    override fun serializer(): Optional<TypeSerializer> {
        return Optional.of(PolymorphisticSerializer(classToType, requiredTypes, reflectMaid))
    }
}

private class PolymorphisticSerializer<T>(
        private val classToType: Map<Class<out T>, String>,
        private val requiredTypes: MutableList<TypeIdentifier>,
        private val reflectMaid: ReflectMaid
) : TypeSerializer {
    @Suppress("UNCHECKED_CAST", "PLATFORM_CLASS_MAPPED_TO_KOTLIN")
    override fun serialize(
            `object`: Any,
            callback: SerializationCallback,
            tracker: SerializationTracker,
            customPrimitiveMappings: CustomPrimitiveMappings,
            debugInformation: DebugInformation
    ): Universal {
        val typeIdentifier = TypeIdentifier.typeIdentifierFor(reflectMaid.resolve(`object`.javaClass))
        val universal = callback.serializeDefinition(
                typeIdentifier,
                `object`,
                SerializationTracker.serializationTracker()
        )
        val immutableMap = universal.toNativeJava() as Map<String, Object>
        val map = immutableMap.toMutableMap()
        val javaClass = `object`.javaClass as Class<out T>
        val type = classToType[javaClass]
        map["typeSerializerType"] = type as Object
        return Universal.fromNativeJava(map)
    }

    override fun requiredTypes(): MutableList<TypeIdentifier> = requiredTypes

    override fun description(): String {
        return "EventStore serializer for ${requiredTypes()}"
    }
}

private class PolymorphisticDeserializer<T>(
        private val typeToClass: Map<String, Class<out T>>,
        private val requiredTypes: MutableList<TypeIdentifier>,
        private val reflectMaid: ReflectMaid
) : TypeDeserializer {

    @Suppress("UNCHECKED_CAST")
    override fun <R : Any?> deserialize(
            input: Universal,
            exceptionTracker: ExceptionTracker,
            injector: Injector,
            callback: DeserializerCallback,
            customPrimitiveMappings: CustomPrimitiveMappings,
            typeIdentifier: TypeIdentifier,
            debugInformation: DebugInformation
    ): R {
        val universalObject = input as UniversalObject
        val typeAsUniversal = universalObject.getField("typeSerializerType")
                .orElseThrow {
                    IllegalArgumentException(
                            "Missing 'typeSerializerType' information in universal object ${input.toNativeJava()}"
                    )
                }
        val type = typeAsUniversal.toNativeJava() as String
        val kClass = typeToClass[type] ?: throw IllegalArgumentException("Unknown SideEffect of type '$type'")
        return callback.deserializeRecursive(
                input,
                TypeIdentifier.typeIdentifierFor(reflectMaid.resolve(kClass)),
                exceptionTracker,
                injector,
                debugInformation
        ) as R
    }

    override fun requiredTypes(): MutableList<TypeIdentifier> = requiredTypes

    override fun description(): String {
        return "EventStore deserializer for ${requiredTypes()}"
    }
}