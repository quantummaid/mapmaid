package de.quantummaid.mapmaid.standardtypeskotlin.mixedcollections

import de.quantummaid.mapmaid.builder.customtypes.SerializationOnlyType
import de.quantummaid.mapmaid.debug.DebugInformation
import de.quantummaid.mapmaid.mapper.serialization.SerializationCallback
import de.quantummaid.mapmaid.mapper.serialization.serializers.TypeSerializer
import de.quantummaid.mapmaid.mapper.serialization.tracker.SerializationTracker
import de.quantummaid.mapmaid.mapper.universal.Universal
import de.quantummaid.mapmaid.mapper.universal.UniversalCollection
import de.quantummaid.mapmaid.mapper.universal.UniversalNull
import de.quantummaid.mapmaid.shared.mapping.CustomPrimitiveMappings
import de.quantummaid.reflectmaid.ReflectMaid
import de.quantummaid.reflectmaid.resolvedtype.ResolvedType
import de.quantummaid.reflectmaid.typescanner.TypeIdentifier

class MixedCollectionSerializer(
    val reflectMaid: ReflectMaid,
    private val typeDeterminer: TypeDeterminer
) : TypeSerializer {

    companion object {
        fun mixedCollectionSerializationOnlyType(
            reflectMaid: ReflectMaid,
            resolvedType: ResolvedType,
            typeDeterminer: TypeDeterminer
        ): SerializationOnlyType<*> {
            val typeSerializer = MixedCollectionSerializer(reflectMaid, typeDeterminer)
            return createSerializer(resolvedType, typeSerializer)
        }
    }

    override fun requiredTypes(): List<TypeIdentifier> {
        return listOf()
    }

    override fun serialize(
        obj: Any?,
        callback: SerializationCallback,
        tracker: SerializationTracker,
        customPrimitiveMappings: CustomPrimitiveMappings,
        debugInformation: DebugInformation
    ): Universal {
        if (obj == null) {
            return UniversalNull.universalNull()
        }
        obj as Collection<*>
        val universalResult = obj.map {
            if (it == null) {
                UniversalNull.universalNull()
            } else {
                val genericType = typeDeterminer.determineType(it)
                val resolvedType = reflectMaid.resolve(genericType)
                callback.serializeDefinition(
                    TypeIdentifier.typeIdentifierFor(resolvedType),
                    it,
                    tracker
                )
            }
        }
        return UniversalCollection.universalCollection(universalResult)
    }

    override fun description(): String = "custom mixed collection serializer"
}
