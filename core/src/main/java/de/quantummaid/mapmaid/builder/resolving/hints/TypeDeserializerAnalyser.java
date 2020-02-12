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

package de.quantummaid.mapmaid.builder.resolving.hints;

import de.quantummaid.mapmaid.builder.detection.DetectionResult;
import de.quantummaid.mapmaid.builder.detection.priority.Prioritized;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.TypeDeserializer;
import de.quantummaid.mapmaid.shared.types.ResolvedType;

import java.util.List;

import static de.quantummaid.mapmaid.builder.resolving.hints.Bucket.bucket;
import static java.util.stream.Collectors.toList;

public final class TypeDeserializerAnalyser {

    public static DetectionResult<TypeDeserializer> analyse(final ResolvedType type,
                                                            final List<Prioritized<TypeDeserializer>> typeDeserializers,
                                                            final Hints hints) {
        final List<TypeDeserializer> deserializers = typeDeserializers.stream()
                .sorted()
                .map(Prioritized::value)
                .collect(toList());
        final Bucket<TypeDeserializer> bucket = bucket(type, deserializers);
        hints.apply(bucket);
        return bucket.expectSingleElement();
    }
}
