package de.quantummaid.mapmaid.standardtypeskotlin

import de.quantummaid.mapmaid.builder.resolving.disambiguator.DisambiguationResult
import de.quantummaid.mapmaid.builder.resolving.framework.Context
import de.quantummaid.mapmaid.builder.resolving.framework.processing.factories.StateFactory
import de.quantummaid.mapmaid.builder.resolving.framework.processing.factories.StateFactoryResult
import de.quantummaid.mapmaid.builder.resolving.framework.processing.factories.StateFactoryResult.stateFactoryResult
import de.quantummaid.mapmaid.builder.resolving.framework.states.detected.Unreasoned.unreasoned
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
import de.quantummaid.mapmaid.shared.mapping.CustomPrimitiveMappings
import de.quantummaid.reflectmaid.resolvedtype.ResolvedType
import java.util.*

private class PairSerializer(
    private val typeIdentifierFirst: TypeIdentifier,
    private val typeIdentifierSecond: TypeIdentifier
) : TypeSerializer {

    override fun requiredTypes(): MutableList<TypeIdentifier> {
        return mutableListOf(typeIdentifierFirst, typeIdentifierSecond)
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
        return "Pair.first and Pair.second"
    }
}

private class PairDeserializer(
    private val typeIdentifierFirst: TypeIdentifier,
    private val typeIdentifierSecond: TypeIdentifier
) : TypeDeserializer {
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
        println("fooo")
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
        return "Pair()"
    }
}

class PairFactory : StateFactory<DisambiguationResult> {

    override fun create(
        typeIdentifier: TypeIdentifier,
        context: Context<DisambiguationResult>,
    ): Optional<StateFactoryResult<DisambiguationResult>> {
        if (typeIdentifier.isVirtual) {
            return Optional.empty()
        }
        val type: ResolvedType = typeIdentifier.realType
        if (type.assignableType() != Pair::class.java) {
            return Optional.empty()
        }

        val typeParameters = type.typeParameters()
        val first = typeParameters[0]
        val second = typeParameters[1]

        val typeIdentifierFirst = TypeIdentifier.typeIdentifierFor(first)
        val typeIdentifierSecond = TypeIdentifier.typeIdentifierFor(second)
        val serializer = PairSerializer(typeIdentifierFirst, typeIdentifierSecond)
        val deserializer = PairDeserializer(typeIdentifierFirst, typeIdentifierSecond)
        context.setManuallyConfiguredResult(DisambiguationResult.duplexResult(serializer, deserializer))
        return Optional.of(stateFactoryResult(unreasoned(context)))
    }
}