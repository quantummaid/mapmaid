package de.quantummaid.mapmaid.standardtypeskotlin

import de.quantummaid.mapmaid.builder.resolving.MapMaidTypeScannerResult
import de.quantummaid.mapmaid.builder.resolving.MapMaidTypeScannerResult.result
import de.quantummaid.mapmaid.builder.resolving.disambiguator.DisambiguationResult.duplexResult
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
import de.quantummaid.reflectmaid.resolvedtype.ResolvedType
import de.quantummaid.reflectmaid.typescanner.Context
import de.quantummaid.reflectmaid.typescanner.TypeIdentifier
import de.quantummaid.reflectmaid.typescanner.factories.StateFactory
import de.quantummaid.reflectmaid.typescanner.states.StatefulDefinition
import de.quantummaid.reflectmaid.typescanner.states.detected.Unreasoned

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

class PairFactory : StateFactory<MapMaidTypeScannerResult> {

    override fun create(
        typeIdentifier: TypeIdentifier,
        context: Context<MapMaidTypeScannerResult>,
    ): StatefulDefinition<MapMaidTypeScannerResult>? {
        if (typeIdentifier.isVirtual()) {
            return null
        }
        val type: ResolvedType = typeIdentifier.realType()
        if (type.assignableType() != Pair::class.java) {
            return null
        }

        val typeParameters = type.typeParameters()
        val first = typeParameters[0]
        val second = typeParameters[1]

        val typeIdentifierFirst = TypeIdentifier.typeIdentifierFor(first)
        val typeIdentifierSecond = TypeIdentifier.typeIdentifierFor(second)
        val serializer = PairSerializer(typeIdentifierFirst, typeIdentifierSecond)
        val deserializer = PairDeserializer(typeIdentifierFirst, typeIdentifierSecond)
        context.setManuallyConfiguredResult(result(duplexResult(serializer, deserializer), typeIdentifier))
        return Unreasoned(context)
    }
}