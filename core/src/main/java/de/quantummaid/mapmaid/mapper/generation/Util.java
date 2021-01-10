package de.quantummaid.mapmaid.mapper.generation;

import de.quantummaid.reflectmaid.GenericType;
import de.quantummaid.reflectmaid.ResolvedType;
import de.quantummaid.reflectmaid.resolver.ResolvedMethod;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.joining;

public final class Util {
    public static final String SERIALIZATION_ONLY_METHOD = "serializing";
    public static final String DESERIALIZATION_ONLY_METHOD = "deserializing";
    public static final String DUPLEX_METHOD = "serializingAndDeserializing";

    public static final String SERIALIZATION_ONLY_CLASS = "SerializationOnlyType";
    public static final String DESERIALIZATION_ONLY_CLASS = "DeserializationOnlyType";
    public static final String DUPLEX_CLASS = "DuplexType";

    public static String renderResolvedType(final ResolvedType resolvedType) {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("GenericType.genericType(");
        final String simpleName = resolvedType.assignableType().getSimpleName();
        final String cleanName;
        if (simpleName.equals("int")) {
            cleanName = "Int";
        } else {
            cleanName = simpleName;
        }
        stringBuilder.append(cleanName);
        stringBuilder.append("::class.java");

        final List<ResolvedType> typeParameters = resolvedType.typeParameters();
        if (!typeParameters.isEmpty()) {
            stringBuilder.append(", ");
            final String parameters = typeParameters.stream()
                    .map(Util::renderResolvedType)
                    .collect(joining(", "));
            stringBuilder.append(parameters);
        }
        stringBuilder.append(")");
        return stringBuilder.toString();
    }

    public static String normalizeMethod(final String name) {
        if (name.startsWith("get")) {
            final String replaced = name.replaceAll("get", "");
            return Character.toLowerCase(replaced.charAt(0)) + replaced.substring(1);
        } else {
            return String.format("%s()", name);
        }
    }

    public static String normalizeMethod(final ResolvedMethod method) {
        final String name = method.name();
        return normalizeMethod(name);
    }

    public static void main(String[] args) {
        final String string = renderResolvedType(GenericType.genericType(Map.class, String.class, int.class).toResolvedType());
        System.out.println(string);
    }
}
