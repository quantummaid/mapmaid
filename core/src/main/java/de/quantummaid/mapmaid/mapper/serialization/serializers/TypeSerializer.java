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

package de.quantummaid.mapmaid.mapper.serialization.serializers;

import de.quantummaid.mapmaid.debug.DebugInformation;
import de.quantummaid.mapmaid.mapper.MappingFunction;
import de.quantummaid.mapmaid.mapper.schema.SchemaCallback;
import de.quantummaid.mapmaid.mapper.serialization.SerializationCallback;
import de.quantummaid.mapmaid.mapper.serialization.tracker.SerializationTracker;
import de.quantummaid.mapmaid.mapper.universal.Universal;
import de.quantummaid.mapmaid.shared.mapping.CustomPrimitiveMappings;
import de.quantummaid.reflectmaid.typescanner.TypeIdentifier;

import java.util.List;

import static de.quantummaid.mapmaid.mapper.serialization.tracker.CircularReferenceException.circularReferenceException;
import static java.lang.String.format;

public interface TypeSerializer extends MappingFunction {
    List<TypeIdentifier> requiredTypes();

    Universal serialize(Object object,
                        SerializationCallback callback,
                        SerializationTracker tracker,
                        CustomPrimitiveMappings customPrimitiveMappings,
                        DebugInformation debugInformation);

    String description();

    default Universal schema(final SchemaCallback schemaCallback) {
        throw new UnsupportedOperationException(format("Schema generation not supported for '%s'", description()));
    }

    default boolean forcesDependenciesToBeObjects() {
        return false;
    }

    default Universal serializeAlreadySeenObject(final Object object,
                                                 final SerializationCallback callback,
                                                 final SerializationTracker tracker,
                                                 final CustomPrimitiveMappings customPrimitiveMappings,
                                                 final DebugInformation debugInformation) {
        throw circularReferenceException(object);
    }
}
