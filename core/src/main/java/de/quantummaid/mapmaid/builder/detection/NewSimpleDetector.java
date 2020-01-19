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

package de.quantummaid.mapmaid.builder.detection;

import de.quantummaid.mapmaid.builder.contextlog.BuildContextLog;
import de.quantummaid.mapmaid.builder.detection.priority.Prioritized;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.TypeDeserializer;
import de.quantummaid.mapmaid.mapper.serialization.serializers.TypeSerializer;
import de.quantummaid.mapmaid.shared.types.ResolvedType;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.Optional;

import static de.quantummaid.mapmaid.shared.validators.NotNullValidator.validateNotNull;
import static java.util.Optional.empty;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class NewSimpleDetector {
    private final List<SerializerFactory> serializerFactories;
    private final List<DeserializerFactory> deserializerFactories;

    public static NewSimpleDetector detector(final List<SerializerFactory> serializerFactories,
                                             final List<DeserializerFactory> deserializerFactories) {
        validateNotNull(serializerFactories, "serializerFactories");
        validateNotNull(deserializerFactories, "deserializerFactories");
        return new NewSimpleDetector(serializerFactories, deserializerFactories);
    }

    public Optional<TypeSerializer> detectSerializer(final ResolvedType type,
                                                     final BuildContextLog parentLog) {
        if (!isSupported(type)) {
            parentLog.logReject(type, "type is not supported because it contains wildcard generics (\"?\")");
            return empty();
        }
        for (final SerializerFactory factory : this.serializerFactories) {
            final BuildContextLog factoryContextLog = parentLog.stepInto(factory.getClass());
            final Optional<TypeSerializer> analyzedClass = factory.analyseForSerializer(type);
            if (analyzedClass.isPresent()) {
                factoryContextLog.log(type, "know how to handle this type");
                return analyzedClass;
            } else {
                factoryContextLog.logReject(type, "do not know how to handle this type");
            }
        }
        return empty();
    }

    public Optional<TypeDeserializer> detectDeserializer(final ResolvedType type,
                                                         final BuildContextLog parentLog) {
        if (!isSupported(type)) {
            parentLog.logReject(type, "type is not supported because it contains wildcard generics (\"?\")");
            return empty();
        }

        /*
        this.deserializerFactories.stream()
                .forEach(deserializerFactory -> );
         */

        for (final DeserializerFactory factory : this.deserializerFactories) {
            final BuildContextLog factoryContextLog = parentLog.stepInto(factory.getClass());
            final List<Prioritized<TypeDeserializer>> deserializers = factory.analyseForDeserializer(type);
            throw new UnsupportedOperationException(); // TODO
            /*
            if (analyzedClass.isPresent()) {
                factoryContextLog.log(type, "know how to handle this type");
                return analyzedClass;
            } else {
                factoryContextLog.logReject(type, "do not know how to handle this type");
            }
             */
        }
        return empty();
    }

    private static boolean isSupported(final ResolvedType resolvedType) {
        if (resolvedType.isWildcard()) {
            return false;
        }
        return resolvedType.typeParameters().stream()
                .allMatch(NewSimpleDetector::isSupported);
    }
}
