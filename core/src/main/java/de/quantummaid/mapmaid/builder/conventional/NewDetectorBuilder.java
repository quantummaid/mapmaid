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

package de.quantummaid.mapmaid.builder.conventional;

import de.quantummaid.mapmaid.builder.detection.DeserializerFactory;
import de.quantummaid.mapmaid.builder.detection.NewSimpleDetector;
import de.quantummaid.mapmaid.builder.detection.SerializerFactory;
import de.quantummaid.mapmaid.builder.detection.customprimitive.CustomPrimitiveDefinitionFactory;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

import static de.quantummaid.mapmaid.builder.conventional.ConventionalDefinitionFactories.nameAndConstructorBasedCustomPrimitiveDefinitionFactory;
import static de.quantummaid.mapmaid.shared.validators.NotNullValidator.validateNotNull;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class NewDetectorBuilder {
    private final List<SerializerFactory> serializerFactories;
    private final List<DeserializerFactory> deserializationFactories;

    public static NewDetectorBuilder detectorBuilder() {
        final NewDetectorBuilder detectorBuilder = new NewDetectorBuilder(new ArrayList<>(10), new ArrayList<>(10));
        return detectorBuilder;
    }

    public NewDetectorBuilder withSerializerFactory(final SerializerFactory serializerFactory) {
        validateNotNull(serializerFactory, "serializerFactory");
        this.serializerFactories.add(serializerFactory);
        return this;
    }

    public NewDetectorBuilder withDeserializerFactory(final DeserializerFactory deserializerFactory) {
        validateNotNull(deserializerFactory, "deserializerFactory");
        this.deserializationFactories.add(deserializerFactory);
        return this;
    }

    public NewDetectorBuilder withNameAndConstructorBasedCustomPrimitiveFactory(final String deserializationMethodName) {
        validateNotNull(deserializationMethodName, "deserializationMethodName");
        final CustomPrimitiveDefinitionFactory factory = nameAndConstructorBasedCustomPrimitiveDefinitionFactory(
                deserializationMethodName);
        return withSerializerFactory(factory).withDeserializerFactory(factory);
    }

    public NewSimpleDetector build() {
        return NewSimpleDetector.detector(this.serializerFactories, this.deserializationFactories);
    }
}
