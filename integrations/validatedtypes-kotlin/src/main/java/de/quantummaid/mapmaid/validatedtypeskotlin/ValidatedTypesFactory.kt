package de.quantummaid.mapmaid.validatedtypeskotlin

import de.quantummaid.mapmaid.builder.MapMaidBuilder
import de.quantummaid.mapmaid.builder.resolving.MapMaidTypeScannerResult
import de.quantummaid.mapmaid.builder.resolving.MapMaidTypeScannerResult.result
import de.quantummaid.mapmaid.builder.resolving.disambiguator.DisambiguationResult
import de.quantummaid.mapmaid.builder.resolving.disambiguator.DisambiguationResult.disambiguationResult
import de.quantummaid.mapmaid.debug.ScanInformationBuilder.scanInformationBuilder
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.customprimitives.CustomPrimitiveByConstructorDeserializer.createDeserializer
import de.quantummaid.mapmaid.mapper.serialization.serializers.customprimitives.CustomPrimitiveSerializer
import de.quantummaid.mapmaid.validatedtypeskotlin.types.ValueType
import de.quantummaid.reflectmaid.typescanner.Context
import de.quantummaid.reflectmaid.typescanner.TypeIdentifier
import de.quantummaid.reflectmaid.typescanner.factories.StateFactory
import de.quantummaid.reflectmaid.typescanner.states.DetectionResult

fun MapMaidBuilder.withSupportForMapMaidValidatedTypes(registerValidationException: Boolean = true): MapMaidBuilder {
    if (registerValidationException) {
        withExceptionIndicatingValidationError(ValidationException::class.java)
    }
    return withSupportForValidatedTypes(ValueType::class.java) {
        it.mappingValue()
    }
}

fun <T> MapMaidBuilder.withSupportForValidatedTypes(
    supertype: Class<T>,
    query: (T) -> Any?
): MapMaidBuilder {
    return withAdvancedSettings {
        val validatedTypesFactory = ValidatedTypesFactory(supertype) {
            @Suppress("UNCHECKED_CAST")
            query(it as T)
        }
        it.withStateFactory(validatedTypesFactory)
    }
}

class ValidatedTypesFactory(
    private val supertype: Class<*>,
    private val query: (Any) -> Any?
) : StateFactory<MapMaidTypeScannerResult> {

    override fun applies(type: TypeIdentifier): Boolean {
        if (type.isVirtual()) {
            return false
        }

        val realType = type.realType()
        val assignableType = realType.assignableType()
        return supertype.isAssignableFrom(assignableType)
    }

    override fun create(type: TypeIdentifier, context: Context<MapMaidTypeScannerResult>) {
        val serializer = ValidatedTypeCustomPrimitiveSerializer(query)
        val realType = type.realType()
        val constructors = realType.constructors()
        val constructor = constructors
            .filter { it.isPublic() }
            .find { it.parameters.size == 1 }
        if (constructor == null) {
            context.setManuallyConfiguredResult(
                DetectionResult.failure<DisambiguationResult>(
                    "type ${realType.description()} does not " +
                            "provide a single one-argument constructor"
                )
                    .mapWithNull { result -> result(result, scanInformationBuilder(type)) }
            )
            return
        }
        val deserializer = createDeserializer(realType, constructor)
        context.setManuallyConfiguredResult(
            result(
                disambiguationResult(
                    serializer,
                    deserializer
                ), type
            )
        )
    }
}

class ValidatedTypeCustomPrimitiveSerializer(private val query: (Any) -> Any?) : CustomPrimitiveSerializer {
    override fun serialize(`object`: Any?): Any? {
        if (`object` == null) {
            return null
        }
        return query(`object`)
    }

    override fun description(): String {
        return "validated type serializer"
    }
}