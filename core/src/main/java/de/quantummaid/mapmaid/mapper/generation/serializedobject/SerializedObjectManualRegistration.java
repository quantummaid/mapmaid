package de.quantummaid.mapmaid.mapper.generation.serializedobject;

import de.quantummaid.mapmaid.mapper.generation.ManualRegistration;
import de.quantummaid.reflectmaid.ResolvedType;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static de.quantummaid.mapmaid.mapper.generation.Util.*;
import static java.lang.String.format;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class SerializedObjectManualRegistration implements ManualRegistration {
    private final String methodName;
    private final String entryClass;
    private final ResolvedType type;
    private final List<ManualField> fields;
    private final String deserialization;

    public static ManualRegistration serializationOnlySerializedObject(final ResolvedType type,
                                                                       final List<ManualField> fields) {
        return new SerializedObjectManualRegistration(
                SERIALIZATION_ONLY_METHOD, SERIALIZATION_ONLY_CLASS, type, fields, null
        );
    }

    public static ManualRegistration deserializationOnlySerializedObject(final ResolvedType type,
                                                                         final List<ManualField> fields,
                                                                         final String deserialization) {
        return new SerializedObjectManualRegistration(
                DESERIALIZATION_ONLY_METHOD, DESERIALIZATION_ONLY_CLASS, type, fields, deserialization
        );
    }

    public static ManualRegistration duplexSerializedObject(final ResolvedType type,
                                                            final List<ManualField> fields,
                                                            final String deserialization) {
        return new SerializedObjectManualRegistration(
                DUPLEX_METHOD, DUPLEX_CLASS, type, fields, deserialization
        );
    }

    @Override
    public String render() {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(format(".%s(%s.serializedObject(%s)", methodName, entryClass, renderResolvedType(type)));
        fields.stream()
                .map(ManualField::render)
                .forEach(stringBuilder::append);
        if (deserialization != null) {
            stringBuilder.append(format(".deserializedUsing { %s }", deserialization));
        }
        stringBuilder.append(")");
        return stringBuilder.toString();
    }

    @Override
    public ManualRegistration merge(final ManualRegistration manualRegistration) {
        if (!(manualRegistration instanceof SerializedObjectManualRegistration)) {
            throw new UnsupportedOperationException("this should never happen");
        }
        final SerializedObjectManualRegistration other = (SerializedObjectManualRegistration) manualRegistration;
        final List<ManualField> mergedFields = new ArrayList<>();

        final List<ManualField> fieldsInCorrectOrder;
        if (methodName.equals(DESERIALIZATION_ONLY_METHOD)) {
            fieldsInCorrectOrder = fields;
        } else {
            fieldsInCorrectOrder = other.fields;
        }

        System.out.println("this.fields = " + this.fields);
        System.out.println("other.fields = " + other.fields);

        for (int i = 0; i < fields.size(); ++i) {
            final String currentName = fieldsInCorrectOrder.get(i).getName();
            final ManualField field1 = fields.stream()
                    .filter(manualField -> manualField.getName().equals(currentName))
                    .findAny().get();
            final ManualField field2 = other.fields.stream()
                    .filter(manualField -> manualField.getName().equals(currentName))
                    .findAny().get();
            final ManualField merge = field1.merge(field2);
            mergedFields.add(merge);
        }
        final String mergedDeserialization;
        if (deserialization != null) {
            mergedDeserialization = deserialization;
        } else {
            mergedDeserialization = other.deserialization;
        }
        return duplexSerializedObject(type, mergedFields, mergedDeserialization);
    }
}
