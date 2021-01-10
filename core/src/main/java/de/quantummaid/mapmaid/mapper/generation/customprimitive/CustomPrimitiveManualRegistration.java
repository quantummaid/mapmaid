package de.quantummaid.mapmaid.mapper.generation.customprimitive;

import de.quantummaid.mapmaid.mapper.generation.ManualRegistration;
import de.quantummaid.mapmaid.mapper.generation.Util;
import de.quantummaid.reflectmaid.ResolvedType;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import static de.quantummaid.mapmaid.mapper.generation.Util.*;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class CustomPrimitiveManualRegistration implements ManualRegistration {
    private final String methodName;
    private final String entryClass;
    private final ResolvedType type;
    private final String serialization;
    private final String deserialization;

    public static ManualRegistration serializationOnlyCustomPrimitive(final ResolvedType type,
                                                                      final String serialization) {
        return new CustomPrimitiveManualRegistration(SERIALIZATION_ONLY_METHOD, SERIALIZATION_ONLY_CLASS, type, serialization, null);
    }

    public static ManualRegistration deserializationOnlyCustomPrimitive(final ResolvedType type,
                                                                        final String deserialization) {
        return new CustomPrimitiveManualRegistration(DESERIALIZATION_ONLY_METHOD, DESERIALIZATION_ONLY_CLASS, type, null, deserialization);
    }

    public static ManualRegistration duplexCustomPrimitive(final ResolvedType type,
                                                           final String serialization,
                                                           final String deserialization) {
        return new CustomPrimitiveManualRegistration(DUPLEX_METHOD, DUPLEX_CLASS, type, serialization, deserialization);
    }

    @Override
    public String render() {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.format(".%s(%s.stringBasedCustomPrimitive(", methodName, entryClass));
        stringBuilder.append(Util.renderResolvedType(type));
        if (serialization != null) {
            stringBuilder.append(String.format(", { %s }", serialization));
        }
        if (deserialization != null) {
            stringBuilder.append(String.format(", { %s }", deserialization));
        }
        stringBuilder.append("))");
        return stringBuilder.toString();
    }

    @Override
    public ManualRegistration merge(final ManualRegistration manualRegistration) {
        if (!(manualRegistration instanceof CustomPrimitiveManualRegistration)) {
            throw new UnsupportedOperationException("this should never happen");
        }
        final CustomPrimitiveManualRegistration other = (CustomPrimitiveManualRegistration) manualRegistration;
        final String mergedSerialization;
        if (serialization != null) {
            mergedSerialization = serialization;
        } else {
            mergedSerialization = other.serialization;
        }

        final String mergedDeserialization;
        if (deserialization != null) {
            mergedDeserialization = deserialization;
        } else {
            mergedDeserialization = other.deserialization;
        }
        return duplexCustomPrimitive(type, mergedSerialization, mergedDeserialization);
    }
}
