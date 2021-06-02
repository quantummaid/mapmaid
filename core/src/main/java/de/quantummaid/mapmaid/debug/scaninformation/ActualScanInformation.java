/*
 * Copyright (c) 2020 Richard Hauswald - https://quantummaid.de/.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package de.quantummaid.mapmaid.debug.scaninformation;

import de.quantummaid.mapmaid.debug.Lingo;
import de.quantummaid.mapmaid.debug.Reason;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.TypeDeserializer;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.customprimitives.CustomPrimitiveDeserializer;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.serializedobjects.SerializedObjectDeserializer;
import de.quantummaid.mapmaid.mapper.serialization.serializers.TypeSerializer;
import de.quantummaid.mapmaid.mapper.serialization.serializers.customprimitives.CustomPrimitiveSerializer;
import de.quantummaid.mapmaid.mapper.serialization.serializers.serializedobject.SerializationField;
import de.quantummaid.mapmaid.mapper.serialization.serializers.serializedobject.SerializedObjectSerializer;
import de.quantummaid.mapmaid.builder.resolving.framework.identifier.TypeIdentifier;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static de.quantummaid.mapmaid.debug.scaninformation.Classification.OTHER;
import static java.lang.String.format;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ActualScanInformation implements ScanInformation {
    private static final String IGNORED_BECAUSE = "\n\t  Ignored because:\n";

    private final TypeIdentifier type;
    private final Reasons reasons;
    private final TypeSerializer serializer;
    private final TypeDeserializer deserializer;
    private final Map<TypeSerializer, List<String>> ignoredSerializers;
    private final Map<SerializationField, List<String>> serializationFields;
    private final Map<TypeDeserializer, List<String>> ignoredDeserializers;

    public static ScanInformation actualScanInformation(
            final TypeIdentifier type,
            final Reasons reasons,
            final TypeSerializer serializer,
            final TypeDeserializer deserializer,
            final Map<TypeSerializer, List<String>> ignoredSerializers,
            final Map<SerializationField, List<String>> serializationFields,
            final Map<TypeDeserializer, List<String>> ignoredDeserializers) {
        return new ActualScanInformation(
                type,
                reasons,
                serializer,
                deserializer,
                ignoredSerializers,
                serializationFields,
                ignoredDeserializers
        );
    }

    @Override
    public String render() {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.type.description());
        stringBuilder.append(":\n");

        final String mode = Lingo.mode(isSerializable(), isDeserializable());
        stringBuilder.append(format("Mode: %s%n", mode));

        if (isSerializable()) {
            stringBuilder.append("How it is serialized:\n");
            stringBuilder.append(format("\t%s", renderSerializer()));

            stringBuilder.append("\nWhy it needs to be serializable:\n");
            stringBuilder.append(this.reasons.dumpSerializationReasons());
            if (!this.ignoredSerializers.isEmpty()) {
                stringBuilder.append(renderIgnoredSerializers());
            }
        }

        if (isDeserializable()) {
            stringBuilder.append("How it is deserialized:\n");
            stringBuilder.append(format("\t%s", renderDeserializer()));

            stringBuilder.append("\nWhy it needs to be deserializable:\n");
            stringBuilder.append(this.reasons.dumpDerializationReasons());

            if (!this.ignoredDeserializers.isEmpty()) {
                stringBuilder.append(renderIgnoredDeserializers());
            }
        }

        return stringBuilder.toString();
    }

    private boolean isSerializable() {
        return this.reasons.hasSerializationReasons();
    }

    private boolean isDeserializable() {
        return this.reasons.hasDeserializationReasons();
    }

    private String renderSerializer() {
        if (this.serializer == null) {
            return "No serializer available";
        } else {
            return this.serializer.description();
        }
    }

    private String renderIgnoredSerializers() {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Ignored features for serialization:\n");
        this.ignoredSerializers.forEach((ignoredSerializer, reasonsForIgnore) -> {
            final String description = ignoredSerializer.description();
            stringBuilder.append("\t- ");
            stringBuilder.append(description);
            stringBuilder.append(IGNORED_BECAUSE);
            stringBuilder.append(renderIgnoreReasons(reasonsForIgnore));
            stringBuilder.append("\n");
        });
        this.serializationFields.forEach((field, reasonsForIgnore) -> {
            final String description = field.describe();
            stringBuilder.append("\t- ");
            stringBuilder.append(description);
            stringBuilder.append(IGNORED_BECAUSE);
            stringBuilder.append(renderIgnoreReasons(reasonsForIgnore));
            stringBuilder.append("\n");
        });
        return stringBuilder.toString();
    }

    private String renderIgnoredDeserializers() {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Ignored features for deserialization:\n");
        this.ignoredDeserializers.forEach((ignoredDeserializer, reasonsForIgnore) -> {
            final String description = ignoredDeserializer.description();
            stringBuilder.append("\t- ");
            stringBuilder.append(description);
            stringBuilder.append(IGNORED_BECAUSE);
            stringBuilder.append(renderIgnoreReasons(reasonsForIgnore));
            stringBuilder.append("\n");
        });
        return stringBuilder.toString();
    }

    private static String renderIgnoreReasons(final List<String> reasons) {
        if (reasons.isEmpty()) {
            return "\t\t- not known";
        }
        return reasons.stream()
                .map(s -> "\t\t- " + s)
                .collect(Collectors.joining("\n"));

    }

    private String renderDeserializer() {
        if (this.deserializer == null) {
            return "No deserializer available";
        } else {
            return this.deserializer.description();
        }
    }

    @Override
    public List<Reason> reasonsForSerialization() {
        return this.reasons.serializationReasons();
    }

    @Override
    public List<Reason> reasonsForDeserialization() {
        return this.reasons.deserializationReasons();
    }

    @Override
    public Classification classification() {
        if (this.deserializer != null) {
            if (this.deserializer instanceof SerializedObjectDeserializer) {
                return Classification.SERIALIZED_OBJECT;
            } else if (this.deserializer instanceof CustomPrimitiveDeserializer) {
                return Classification.CUSTOM_PRIMITIVE;
            } else {
                return OTHER;
            }
        }
        if (this.serializer != null) {
            if (this.serializer instanceof SerializedObjectSerializer) {
                return Classification.SERIALIZED_OBJECT;
            } else if (this.serializer instanceof CustomPrimitiveSerializer) {
                return Classification.CUSTOM_PRIMITIVE;
            } else {
                return OTHER;
            }
        }
        throw new UnsupportedOperationException("Unable to do classification");
    }
}
