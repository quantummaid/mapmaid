package de.quantummaid.mapmaid.standardtypeskotlin

import de.quantummaid.mapmaid.builder.MapMaidBuilder
import de.quantummaid.mapmaid.builder.resolving.MapMaidTypeScannerResult
import de.quantummaid.mapmaid.builder.resolving.MapMaidTypeScannerResult.result
import de.quantummaid.mapmaid.builder.resolving.disambiguator.DisambiguationResult.duplexResult
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.TypeDeserializer
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.customprimitives.CustomPrimitiveDeserializer
import de.quantummaid.mapmaid.mapper.serialization.serializers.TypeSerializer
import de.quantummaid.mapmaid.mapper.serialization.serializers.customprimitives.CustomPrimitiveSerializer
import de.quantummaid.mapmaid.standardtypeskotlin.CustomFactory.Companion.createCustomFactory
import de.quantummaid.mapmaid.standardtypeskotlin.mixedcollections.withMixedCollectionSerializationSupport
import de.quantummaid.reflectmaid.ReflectMaid
import de.quantummaid.reflectmaid.resolvedtype.ResolvedType
import de.quantummaid.reflectmaid.typescanner.Context
import de.quantummaid.reflectmaid.typescanner.TypeIdentifier
import de.quantummaid.reflectmaid.typescanner.TypeIdentifier.Companion.typeIdentifierFor
import de.quantummaid.reflectmaid.typescanner.factories.StateFactory
import java.time.Duration
import java.time.Instant

fun MapMaidBuilder.withAllCommonTypesPreRegistered(): MapMaidBuilder {
    withSupportForStandardKotlinTypes(true)
    withMixedCollectionSerializationSupport()
    serializingAndDeserializing(String::class.java)
    serializingAndDeserializing(Int::class.java)
    serializingAndDeserializing(Long::class.java)
    serializingAndDeserializing(Double::class.java)
    serializingAndDeserializing(Float::class.java)
    serializingAndDeserializing(Char::class.java)
    return this
}

fun MapMaidBuilder.withSupportForStandardKotlinTypes(preRegisterTypes: Boolean = false): MapMaidBuilder {
    val reflectMaid = reflectMaid()
    this.withAdvancedSettings { builder ->
        builder
            .withStateFactory(PairFactory())
            .withStateFactory(
                createCustomFactory<Instant>(
                    reflectMaid,
                    SimpleSerializer<Instant>("toString()") { it.toString() },
                    SimpleDeserializer<Instant>("Instant.parse()") { Instant.parse(it) }
                )
            )
            .withStateFactory(
                createCustomFactory<Duration>(
                    reflectMaid,
                    SimpleSerializer<Duration>("toString()") { it.toString() },
                    SimpleDeserializer<Duration>("Duration.parse()") { Duration.parse(it) }
                )
            )
    }
    if (preRegisterTypes) {
        serializingAndDeserializing<Duration>()
        serializingAndDeserializing<Instant>()
    }
    return this
}

private class SimpleSerializer<T>(
    private val description: String,
    private val serializer: (T?) -> String
) : CustomPrimitiveSerializer {

    override fun serialize(`object`: Any?): Any {
        @Suppress("UNCHECKED_CAST")
        return serializer.invoke(`object` as T?)
    }

    override fun description(): String {
        return description
    }
}

private class SimpleDeserializer<T : Any>(
    private val description: String,
    private val deserializer: (String?) -> T
) : CustomPrimitiveDeserializer {

    override fun deserialize(value: Any?): Any {
        @Suppress("UNCHECKED_CAST")
        return deserializer.invoke(value as String)
    }

    override fun description(): String {
        return description;
    }
}

private class CustomFactory(
    targetType: ResolvedType,
    private val serializer: TypeSerializer,
    private val deserializer: TypeDeserializer
) : StateFactory<MapMaidTypeScannerResult> {
    private val targetTypeIdentifier = typeIdentifierFor(targetType)

    companion object {
        inline fun <reified T : Any> createCustomFactory(
            reflectMaid: ReflectMaid,
            serializer: TypeSerializer,
            deserializer: TypeDeserializer
        ): CustomFactory {
            val targetType = reflectMaid.resolve<T>()
            return CustomFactory(targetType, serializer, deserializer)
        }
    }

    override fun applies(type: TypeIdentifier): Boolean {
        return targetTypeIdentifier == type
    }

    override fun create(
        type: TypeIdentifier,
        context: Context<MapMaidTypeScannerResult>,
    ) {
        context.setManuallyConfiguredResult(result(duplexResult(serializer, deserializer), type))
    }
}