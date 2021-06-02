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

import de.quantummaid.mapmaid.collections.BiMap;
import de.quantummaid.mapmaid.debug.DebugInformation;
import de.quantummaid.mapmaid.mapper.schema.SchemaCallback;
import de.quantummaid.mapmaid.mapper.serialization.SerializationCallback;
import de.quantummaid.mapmaid.mapper.serialization.serializers.TypeSerializer;
import de.quantummaid.mapmaid.mapper.serialization.tracker.SerializationTracker;
import de.quantummaid.mapmaid.mapper.universal.Universal;
import de.quantummaid.mapmaid.polymorphy.finiteresolver.FiniteTypeResolver;
import de.quantummaid.mapmaid.builder.resolving.framework.identifier.TypeIdentifier;
import de.quantummaid.mapmaid.shared.mapping.CustomPrimitiveMappings;
import de.quantummaid.reflectmaid.resolvedtype.ResolvedType;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static de.quantummaid.mapmaid.mapper.schema.SchemaSupport.schemaForPolymorphicParent;
import static de.quantummaid.mapmaid.mapper.serialization.tracker.SerializationTracker.serializationTracker;
import static de.quantummaid.mapmaid.polymorphy.TypeFieldNormalizer.determineTypeField;
import static de.quantummaid.mapmaid.polymorphy.finiteresolver.FiniteTypeResolver.finiteTypeResolver;
import static de.quantummaid.mapmaid.builder.resolving.framework.identifier.TypeIdentifier.typeIdentifierFor;
import static java.lang.String.format;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class PolymorphicSerializer implements TypeSerializer {
    private final TypeIdentifier superType;
    private final FiniteTypeResolver resolver;
    private final BiMap<String, TypeIdentifier> nameToType;
    private final String typeField;

    public static PolymorphicSerializer polymorphicSerializer(final TypeIdentifier superType,
                                                              final List<ResolvedType> subTypes,
                                                              final BiMap<String, TypeIdentifier> nameToType,
                                                              final String typeField) {
        final FiniteTypeResolver typeResolver = finiteTypeResolver(subTypes);
        return new PolymorphicSerializer(superType, typeResolver, nameToType, typeField);
    }

    @Override
    public List<TypeIdentifier> requiredTypes() {
        return nameToType.values();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Universal serialize(final Object object,
                               final SerializationCallback callback,
                               final SerializationTracker tracker,
                               final CustomPrimitiveMappings customPrimitiveMappings,
                               final DebugInformation debugInformation) {
        final ResolvedType resolvedType = resolver.determineType(object);
        final TypeIdentifier implementationType = typeIdentifierFor(resolvedType);
        final Universal universal = callback.serializeDefinition(implementationType, object, serializationTracker());
        final Map<String, Object> immutableMap = (Map<String, Object>) universal.toNativeJava();
        final Map<String, Object> mutableMap = new LinkedHashMap<>(immutableMap);
        final String type = nameToType.reverseLookup(implementationType)
                .orElseThrow(() -> new IllegalArgumentException(format("Unknown event of type '%s'", implementationType.description())));
        final String normalizedTypeField = determineTypeField(typeField, mutableMap.keySet());
        mutableMap.put(normalizedTypeField, type);
        return Universal.fromNativeJava(mutableMap);
    }

    @Override
    public boolean forcesDependenciesToBeObjects() {
        return true;
    }

    @Override
    public String description() {
        return format("polymorphic serializer for %s", this.superType.description());
    }

    @Override
    public Universal schema(final SchemaCallback schemaCallback) {
        return schemaForPolymorphicParent(nameToType, typeField, schemaCallback);
    }
}
