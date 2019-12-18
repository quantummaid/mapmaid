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

package de.quantummaid.mapmaid.mapper.deserialization.deserializers;

import de.quantummaid.mapmaid.mapper.deserialization.DeserializerCallback;
import de.quantummaid.mapmaid.mapper.deserialization.WrongInputStructureException;
import de.quantummaid.mapmaid.mapper.deserialization.validation.ExceptionTracker;
import de.quantummaid.mapmaid.mapper.injector.Injector;
import de.quantummaid.mapmaid.mapper.universal.Universal;
import de.quantummaid.mapmaid.shared.mapping.CustomPrimitiveMappings;
import de.quantummaid.mapmaid.shared.types.ResolvedType;

import java.util.List;

public interface TypeDeserializer {
    List<ResolvedType> requiredTypes();

    <T> T deserialize(Universal input,
                      ExceptionTracker exceptionTracker,
                      Injector injector,
                      DeserializerCallback callback,
                      CustomPrimitiveMappings customPrimitiveMappings);

    Class<? extends Universal> universalRequirement();

    String classification();

    static <T extends Universal> T castSafely(final Universal universalType,
                                              final Class<T> type,
                                              final ExceptionTracker exceptionTracker) {
        if (!type.isInstance(universalType)) {
            throw WrongInputStructureException.wrongInputStructureException(type, universalType, exceptionTracker.getPosition());
        }
        return type.cast(universalType);
    }
}
