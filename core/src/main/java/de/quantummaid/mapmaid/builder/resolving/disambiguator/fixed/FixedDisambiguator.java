/*
 * Copyright (c) 2019 Richard Hauswald - https://quantummaid.de/.
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

package de.quantummaid.mapmaid.builder.resolving.disambiguator.fixed;

import de.quantummaid.mapmaid.builder.detection.DetectionResult;
import de.quantummaid.mapmaid.builder.resolving.disambiguator.DisambiguationResult;
import de.quantummaid.mapmaid.builder.resolving.disambiguator.Disambiguator;
import de.quantummaid.mapmaid.builder.resolving.disambiguator.SerializersAndDeserializers;
import de.quantummaid.mapmaid.builder.resolving.disambiguator.fixed.deserialize.DeserializerDisambiguator;
import de.quantummaid.mapmaid.builder.resolving.disambiguator.fixed.serializer.SerializerDisambiguator;
import de.quantummaid.mapmaid.builder.resolving.disambiguator.symmetry.SerializedObjectOptions;
import de.quantummaid.mapmaid.debug.Lingo;
import de.quantummaid.mapmaid.debug.MapMaidException;
import de.quantummaid.mapmaid.debug.ScanInformationBuilder;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.TypeDeserializer;
import de.quantummaid.mapmaid.mapper.serialization.serializers.TypeSerializer;
import de.quantummaid.mapmaid.shared.types.ResolvedType;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import static de.quantummaid.mapmaid.builder.detection.DetectionResult.success;
import static de.quantummaid.mapmaid.builder.resolving.disambiguator.DisambiguationResult.*;
import static de.quantummaid.mapmaid.builder.resolving.disambiguator.SerializersAndDeserializers.serializersAndDeserializers;
import static de.quantummaid.mapmaid.debug.Lingo.*;
import static java.lang.String.format;
import static java.util.Collections.singletonList;
import static java.util.Objects.nonNull;

// TODO always delegate to default
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class FixedDisambiguator implements Disambiguator {
    private final Disambiguator defaultDisambiguator;
    private final SerializerDisambiguator serializerDisambiguator;
    private final DeserializerDisambiguator deserializerDisambiguator;

    @Override
    public DetectionResult<DisambiguationResult> disambiguate(final ResolvedType type,
                                                              final SerializedObjectOptions serializedObjectOptions,
                                                              final SerializersAndDeserializers serializersAndDeserializers,
                                                              final ScanInformationBuilder scanInformationBuilder) {
        if (serializersAndDeserializers.serializationOnly()) {
            ensureNonNull(this.serializerDisambiguator, type, SERIALIZATION_ONLY);
            final TypeSerializer serializer = this.serializerDisambiguator.disambiguate(serializersAndDeserializers.serializers());
            return success(serializationOnlyResult(serializer));
        }

        if (serializersAndDeserializers.deserializationOnly()) {
            ensureNonNull(this.deserializerDisambiguator, type, DESERIALIZATION_ONLY);
            final TypeDeserializer deserializer = this.deserializerDisambiguator.disambiguate(serializersAndDeserializers.deserializers());
            return success(deserializationOnlyResult(deserializer));
        }

        if (nonNull(this.serializerDisambiguator) && nonNull(this.deserializerDisambiguator)) {
            final TypeSerializer serializer = this.serializerDisambiguator.disambiguate(serializersAndDeserializers.serializers());
            final TypeDeserializer deserializer = this.deserializerDisambiguator.disambiguate(serializersAndDeserializers.deserializers());
            return success(duplexResult(serializer, deserializer));
        }

        if (nonNull(this.serializerDisambiguator)) {
            final TypeSerializer serializer = this.serializerDisambiguator.disambiguate(serializersAndDeserializers.serializers());
            final SerializersAndDeserializers fixedSerializersAndDeserializers = serializersAndDeserializers(
                    singletonList(serializer), serializersAndDeserializers.deserializers());
            // TODO
            return this.defaultDisambiguator.disambiguate(type, null, fixedSerializersAndDeserializers, scanInformationBuilder);
        }

        if (nonNull(this.deserializerDisambiguator)) {
            final TypeDeserializer deserializer = this.deserializerDisambiguator.disambiguate(serializersAndDeserializers.deserializers());
            final SerializersAndDeserializers fixedSerializersAndDeserializers = serializersAndDeserializers(
                    serializersAndDeserializers.serializers(), singletonList(deserializer));
            // TODO
            return this.defaultDisambiguator.disambiguate(type, null, fixedSerializersAndDeserializers, scanInformationBuilder);
        }

        throw new UnsupportedOperationException("This should never happen");
    }

    private void ensureNonNull(final Object object,
                               final ResolvedType type,
                               final String mode) {
        if (object != null) {
            return;
        }

        throw MapMaidException.mapMaidException(format(
                "Type '%s' is only required %s, but a %s was configured for it as %s. This indicates an error.",
                type.description(), mode, DISAMBIGUATOR, mode()));
    }

    private String mode() {
        return Lingo.mode(nonNull(this.serializerDisambiguator), nonNull(this.deserializerDisambiguator));
    }
}
