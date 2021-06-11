package de.quantummaid.mapmaid.mapper.serialization.supertypes;

import de.quantummaid.mapmaid.mapper.serialization.serializers.TypeSerializer;
import de.quantummaid.reflectmaid.resolvedtype.ResolvedType;
import de.quantummaid.reflectmaid.typescanner.TypeIdentifier;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

import static de.quantummaid.mapmaid.debug.MapMaidException.mapMaidException;
import static de.quantummaid.mapmaid.shared.validators.NotNullValidator.validateNotNull;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class SupertypeSerializers {
    private final Map<TypeIdentifier, TypeSerializer> superTypeSerializers;

    public static SupertypeSerializers superTypeSerializers(final Map<TypeIdentifier, TypeSerializer> superTypeSerializers) {
        validateNotNull(superTypeSerializers, "superTypeSerializers");
        return new SupertypeSerializers(superTypeSerializers);
    }

    public TypeSerializer supertypeSerializer(final TypeIdentifier typeIdentifier) {
        if (!superTypeSerializers.containsKey(typeIdentifier)) {
            throw mapMaidException("supertype " + typeIdentifier.description() + " not found - this should never happen");
        }
        return superTypeSerializers.get(typeIdentifier);
    }

    public List<TypeIdentifier> detectSuperTypeSerializersFor(final TypeIdentifier typeIdentifier) {
        if (typeIdentifier.isVirtual()) {
            return emptyList();
        }
        final ResolvedType resolvedType = typeIdentifier.realType();
        return resolvedType
                .allSupertypes()
                .stream()
                .map(TypeIdentifier::typeIdentifierFor)
                .filter(superTypeSerializers::containsKey)
                .collect(toList());
    }
}
