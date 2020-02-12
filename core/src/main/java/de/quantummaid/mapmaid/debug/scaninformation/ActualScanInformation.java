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

import de.quantummaid.mapmaid.builder.resolving.Reason;
import de.quantummaid.mapmaid.debug.Lingo;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.TypeDeserializer;
import de.quantummaid.mapmaid.mapper.serialization.serializers.TypeSerializer;
import de.quantummaid.mapmaid.shared.types.ResolvedType;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.String.format;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ActualScanInformation implements ScanInformation {
    private final ResolvedType type;
    private final List<Reason> deserializationReasons;
    private final List<Reason> serializationReasons;
    private final TypeSerializer serializer;
    private final TypeDeserializer deserializer;
    private final Map<TypeSerializer, List<String>> serializers;
    private final Map<TypeDeserializer, List<String>> deserializers;

    public static ScanInformation actualScanInformation(final ResolvedType type,
                                                        final List<Reason> deserializationReasons,
                                                        final List<Reason> serializationReasons,
                                                        final TypeSerializer serializer,
                                                        final TypeDeserializer deserializer,
                                                        final Map<TypeSerializer, List<String>> serializers,
                                                        final Map<TypeDeserializer, List<String>> deserializers) {
        return new ActualScanInformation(
                type,
                deserializationReasons,
                serializationReasons,
                serializer,
                deserializer,
                serializers,
                deserializers);
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
            this.serializationReasons.forEach(reason -> stringBuilder.append(format("\t- %s%n", reason.render())));

            stringBuilder.append("Ignored features for serialization:\n");
            stringBuilder.append(renderIgnoredSerializers());
        }

        if (isDeserializable()) {
            stringBuilder.append("How it is deserialized:\n");
            stringBuilder.append(format("\t%s", renderDeserializer()));

            stringBuilder.append("\nWhy it needs to be deserializable:\n");
            this.deserializationReasons.forEach(reason -> stringBuilder.append(format("\t- %s%n", reason.render())));

            stringBuilder.append("Ignored features for deserialization:\n");
            stringBuilder.append(renderIgnoredDeserializers());
        }

        return stringBuilder.toString();
    }

    private boolean isSerializable() {
        return !this.serializationReasons.isEmpty(); // TODO
    }

    private boolean isDeserializable() {
        return !this.deserializationReasons.isEmpty(); // TODO
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
        for (final TypeSerializer serializer : this.serializers.keySet()) {
            if (serializer.equals(this.serializer)) {
                continue;
            }
            final String description = serializer.description();
            stringBuilder.append("\t- ");
            stringBuilder.append(description);
            stringBuilder.append("\n\t  Ignored because:\n");
            final List<String> reasons = this.serializers.get(serializer);
            stringBuilder.append(renderIgnoreReasons(reasons));
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }

    private String renderIgnoredDeserializers() {
        final StringBuilder stringBuilder = new StringBuilder();
        for (final TypeDeserializer deserializer : this.deserializers.keySet()) {
            if (deserializer.equals(this.deserializer)) {
                continue;
            }
            final String description = deserializer.description();
            stringBuilder.append("\t- ");
            stringBuilder.append(description);
            stringBuilder.append("\n\t  Ignored because:\n");
            final List<String> reasons = this.deserializers.get(deserializer);
            stringBuilder.append(renderIgnoreReasons(reasons));
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }

    private String renderIgnoreReasons(final List<String> reasons) {
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
}
