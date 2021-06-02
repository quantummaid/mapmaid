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

package de.quantummaid.mapmaid.polymorphy;

import de.quantummaid.mapmaid.builder.MapMaidConfiguration;
import de.quantummaid.mapmaid.collections.BiMap;
import de.quantummaid.mapmaid.builder.resolving.framework.identifier.TypeIdentifier;

import java.util.List;
import java.util.Map;

import static de.quantummaid.mapmaid.collections.BiMap.biMap;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

public final class PolymorphicUtils {

    private PolymorphicUtils() {
    }

    public static BiMap<String, TypeIdentifier> nameToIdentifier(final List<TypeIdentifier> subTypes,
                                                                 final MapMaidConfiguration mapMaidConfiguration) {
        final PolymorphicTypeIdentifierExtractor extractor = mapMaidConfiguration.getTypeIdentifierExtractor();
        final Map<String, TypeIdentifier> nameToTypeMap = subTypes.stream()
                .collect(toMap(extractor::extract, identity()));
        return biMap(nameToTypeMap);
    }
}
