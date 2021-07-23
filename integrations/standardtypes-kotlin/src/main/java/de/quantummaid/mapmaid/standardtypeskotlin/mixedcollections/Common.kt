package de.quantummaid.mapmaid.standardtypeskotlin.mixedcollections

import de.quantummaid.mapmaid.builder.MapMaidBuilder
import de.quantummaid.mapmaid.builder.customtypes.SerializationOnlyType
import de.quantummaid.mapmaid.builder.customtypes.SerializationOnlyType.serializationOnlyType
import de.quantummaid.mapmaid.mapper.serialization.serializers.TypeSerializer
import de.quantummaid.mapmaid.standardtypeskotlin.mixedcollections.MixedCollectionSerializer.Companion.mixedCollectionSerializationOnlyType
import de.quantummaid.mapmaid.standardtypeskotlin.mixedcollections.MixedMapSerializer.Companion.mixedMapSerializationOnlyType
import de.quantummaid.reflectmaid.GenericType
import de.quantummaid.reflectmaid.GenericType.Companion.genericType
import de.quantummaid.reflectmaid.resolvedtype.ResolvedType
import de.quantummaid.reflectmaid.typescanner.TypeIdentifier

fun MapMaidBuilder.withMixedCollectionSerializationSupport(
    customMapping: CustomTypeMapping = CustomTypeMapping { null }
): MapMaidBuilder {
    val typeDeterminer = SimpleTypeDeterminer(customMapping)
    return withMixedCollectionSerializationSupport(
        listOf(
            genericType<Collection<Any>>(),
            genericType<List<Any>>(),
            genericType<Set<Any>>()
        ),
        listOf(
            genericType<Map<String, Any>>()
        ),
        typeDeterminer
    )
}

fun MapMaidBuilder.withMixedCollectionSerializationSupport(
    collectionTypes: List<GenericType<out Collection<*>>>,
    mapTypes: List<GenericType<out Map<*, *>>>,
    typeDeterminer: TypeDeterminer
): MapMaidBuilder {
    val reflectMaid = reflectMaid()
    collectionTypes.forEach {
        val resolvedType = reflectMaid.resolve(it)
        val collectionSerializer = mixedCollectionSerializationOnlyType(reflectMaid, resolvedType, typeDeterminer)
        serializing(collectionSerializer)
    }
    mapTypes.forEach {
        val resolvedType = reflectMaid.resolve(it)
        val mapSerializer = mixedMapSerializationOnlyType(reflectMaid, resolvedType, typeDeterminer)
        serializing(mapSerializer)
    }
    return this
}

interface TypeDeterminer {
    fun determineType(collectionItemValue: Any): GenericType<out Any>
}

fun interface CustomTypeMapping {
    fun map(value: Any): GenericType<out Any>?
}

class SimpleTypeDeterminer(val customMapping: CustomTypeMapping = CustomTypeMapping { null }) : TypeDeterminer {
    override fun determineType(collectionItemValue: Any): GenericType<out Any> {
        return when (collectionItemValue) {
            is Map<*, *> -> genericType<Map<String, Any>>()
            is Collection<*> -> genericType<Collection<Any>>()
            else -> {
                customMapping.map(collectionItemValue)
                    ?: genericType(collectionItemValue.javaClass)
            }
        }
    }
}

internal fun createSerializer(
    resolvedType: ResolvedType,
    typeSerializer: TypeSerializer,
): SerializationOnlyType<Any> {
    val typeIdentifier = TypeIdentifier.typeIdentifierFor(resolvedType)
    return serializationOnlyType(typeIdentifier, typeSerializer)
}
