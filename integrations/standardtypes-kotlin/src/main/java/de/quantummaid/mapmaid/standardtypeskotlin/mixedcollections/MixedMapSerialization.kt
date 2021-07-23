package de.quantummaid.mapmaid.standardtypeskotlin.mixedcollections

import de.quantummaid.mapmaid.builder.customtypes.SerializationOnlyType
import de.quantummaid.mapmaid.debug.DebugInformation
import de.quantummaid.mapmaid.mapper.serialization.SerializationCallback
import de.quantummaid.mapmaid.mapper.serialization.serializers.TypeSerializer
import de.quantummaid.mapmaid.mapper.serialization.tracker.SerializationTracker
import de.quantummaid.mapmaid.mapper.universal.Universal
import de.quantummaid.mapmaid.mapper.universal.UniversalNull
import de.quantummaid.mapmaid.mapper.universal.UniversalObject
import de.quantummaid.mapmaid.shared.mapping.CustomPrimitiveMappings
import de.quantummaid.reflectmaid.ReflectMaid
import de.quantummaid.reflectmaid.resolvedtype.ResolvedType
import de.quantummaid.reflectmaid.typescanner.TypeIdentifier

class MixedMapSerializer(
    val reflectMaid: ReflectMaid,
    private val typeDeterminer: TypeDeterminer
) : TypeSerializer {
    companion object {
        fun mixedMapSerializationOnlyType(
            reflectMaid: ReflectMaid,
            resolvedType: ResolvedType,
            typeDeterminer: TypeDeterminer
        ): SerializationOnlyType<*> {
            val typeSerializer = MixedMapSerializer(reflectMaid, typeDeterminer)
            return createSerializer(resolvedType, typeSerializer)
        }
    }

    override fun requiredTypes(): List<TypeIdentifier> {
        return listOf()
    }

    @Suppress("UNCHECKED_CAST")
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
        val map = obj as Map<String, Any>
        if (obj.isEmpty()) {
            return UniversalNull.universalNull()
        }
        val result = map.mapValues { mapEntry ->
            val mapEntryValue = mapEntry.value
            val genericType = typeDeterminer.determineType(mapEntryValue)
            val resolvedType = reflectMaid.resolve(genericType)
            callback.serializeDefinition(
                TypeIdentifier.typeIdentifierFor(resolvedType),
                mapEntryValue,
                tracker
            )
        }
        return UniversalObject.universalObject(result)
    }

    override fun description(): String = "custom mixed map serializer"
}
