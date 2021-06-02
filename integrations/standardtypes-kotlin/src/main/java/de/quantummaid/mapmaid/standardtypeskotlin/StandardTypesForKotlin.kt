package de.quantummaid.mapmaid.standardtypeskotlin

import de.quantummaid.mapmaid.builder.MapMaidBuilder
import de.quantummaid.mapmaid.builder.resolving.MapMaidTypeScannerResult
import de.quantummaid.mapmaid.builder.resolving.MapMaidTypeScannerResult.result
import de.quantummaid.mapmaid.builder.resolving.disambiguator.DisambiguationResult.duplexResult
import de.quantummaid.mapmaid.builder.resolving.framework.Context
import de.quantummaid.mapmaid.builder.resolving.framework.identifier.TypeIdentifier
import de.quantummaid.mapmaid.builder.resolving.framework.processing.factories.StateFactory
import de.quantummaid.mapmaid.builder.resolving.framework.states.StatefulDefinition
import de.quantummaid.mapmaid.builder.resolving.framework.states.detected.Unreasoned.unreasoned
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.TypeDeserializer
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.customprimitives.CustomPrimitiveDeserializer
import de.quantummaid.mapmaid.mapper.serialization.serializers.TypeSerializer
import de.quantummaid.mapmaid.mapper.serialization.serializers.customprimitives.CustomPrimitiveSerializer
import de.quantummaid.mapmaid.standardtypeskotlin.CustomFactory.Companion.createCustomFactory
import de.quantummaid.reflectmaid.ReflectMaid
import de.quantummaid.reflectmaid.resolvedtype.ResolvedType
import java.time.Duration
import java.time.Instant
import java.util.*

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
        return description;
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
    private val targetType: ResolvedType,
    private val serializer: TypeSerializer,
    private val deserializer: TypeDeserializer
) : StateFactory<MapMaidTypeScannerResult> {

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

    override fun create(
        typeIdentifier: TypeIdentifier,
        context: Context<MapMaidTypeScannerResult>,
    ): Optional<StatefulDefinition<MapMaidTypeScannerResult>> {
        if (typeIdentifier.isVirtual) {
            return Optional.empty()
        }
        val type: ResolvedType = typeIdentifier.realType
        if (type != targetType) {
            return Optional.empty()
        }
        context.setManuallyConfiguredResult(result(duplexResult(serializer, deserializer), typeIdentifier))
        return Optional.of(unreasoned(context))
    }
}