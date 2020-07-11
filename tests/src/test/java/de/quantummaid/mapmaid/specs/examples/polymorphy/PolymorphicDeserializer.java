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

package de.quantummaid.mapmaid.specs.examples.polymorphy;

import de.quantummaid.mapmaid.collections.BiMap;
import de.quantummaid.mapmaid.debug.DebugInformation;
import de.quantummaid.mapmaid.mapper.deserialization.DeserializerCallback;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.TypeDeserializer;
import de.quantummaid.mapmaid.mapper.deserialization.validation.ExceptionTracker;
import de.quantummaid.mapmaid.mapper.injector.Injector;
import de.quantummaid.mapmaid.mapper.schema.SchemaCallback;
import de.quantummaid.mapmaid.mapper.universal.Universal;
import de.quantummaid.mapmaid.mapper.universal.UniversalObject;
import de.quantummaid.mapmaid.shared.identifier.TypeIdentifier;
import de.quantummaid.mapmaid.shared.mapping.CustomPrimitiveMappings;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static de.quantummaid.mapmaid.mapper.schema.SchemaSupport.schemaForPolymorphicParent;
import static java.lang.String.format;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class PolymorphicDeserializer implements TypeDeserializer {
    private final TypeIdentifier typeIdentifier;
    private final BiMap<String, TypeIdentifier> nameToType;
    private final String typeField;

    public static PolymorphicDeserializer polymorphicDeserializer(
            final TypeIdentifier typeIdentifier,
            final BiMap<String, TypeIdentifier> nameToType,
            final String typeField) {
        return new PolymorphicDeserializer(typeIdentifier, nameToType, typeField);
    }

    @Override
    public List<TypeIdentifier> requiredTypes() {
        return nameToType.values();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T deserialize(final Universal input,
                             final ExceptionTracker exceptionTracker,
                             final Injector injector,
                             final DeserializerCallback callback,
                             final CustomPrimitiveMappings customPrimitiveMappings,
                             final TypeIdentifier typeIdentifier,
                             final DebugInformation debugInformation) {
        final UniversalObject universalObject = (UniversalObject) input;
        final Universal typeAsUniversal = universalObject.getField(typeField)
                .orElseThrow(() -> new IllegalArgumentException(format(
                        "Missing '%s' information in universal object %s", typeField, input.toNativeJava())));
        final String type = (String) typeAsUniversal.toNativeJava();
        final TypeIdentifier implementation = nameToType.lookup(type)
                .orElseThrow(() -> {
                    final String subtypeIdentifier = String.join(", ", nameToType.keys());
                    return new IllegalArgumentException(format(
                            "Unknown type '%s' (needs to be a subtype of %s, known subtype identifiers: [%s])",
                            type, typeIdentifier.description(), subtypeIdentifier));
                });

        return (T) callback.deserializeRecursive(
                input,
                implementation,
                exceptionTracker,
                injector,
                debugInformation);
    }

    @Override
    public String description() {
        return format("polymorphic deserializer for %s", this.typeIdentifier.description());
    }

    @Override
    public Universal schema(final SchemaCallback schemaCallback) {
        return schemaForPolymorphicParent(nameToType, typeField, schemaCallback);
    }
}
