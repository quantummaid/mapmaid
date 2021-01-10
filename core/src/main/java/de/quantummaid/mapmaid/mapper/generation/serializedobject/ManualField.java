package de.quantummaid.mapmaid.mapper.generation.serializedobject;

import de.quantummaid.reflectmaid.ResolvedType;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import static de.quantummaid.mapmaid.mapper.generation.Util.renderResolvedType;

@ToString
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ManualField {
    private final ResolvedType type;
    private final String name;
    private final String query;

    public static ManualField serializableField(final ResolvedType type, final String name, final String query) {
        return new ManualField(type, name, query);
    }

    public static ManualField nonSerializableField(final ResolvedType type, final String name) {
        return new ManualField(type, name, null);
    }

    public String render() {
        if (query != null) {
            return String.format(".withField(\"%s\", %s, { %s })",
                    name,
                    renderResolvedType(type),
                    query
            );
        } else {
            return String.format(".withField(\"%s\", %s)",
                    name,
                    renderResolvedType(type)
            );
        }
    }

    public String getName() {
        return name;
    }

    public ManualField merge(final ManualField manualField) {
        if (query != null && manualField.query == null) {
            return this;
        } else if (query == null && manualField.query != null) {
            return manualField;
        } else {
            throw new UnsupportedOperationException("this should never happen");
        }
    }
}
