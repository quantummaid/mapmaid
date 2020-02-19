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

import de.quantummaid.mapmaid.builder.detection.SimpleDetector;
import de.quantummaid.mapmaid.builder.detection.customprimitive.deserialization.CustomPrimitiveDeserializationDetector;
import de.quantummaid.mapmaid.builder.detection.customprimitive.serialization.CustomPrimitiveSerializationDetector;
import de.quantummaid.mapmaid.builder.detection.serializedobject.deserialization.SerializedObjectDeserializationDetector;
import de.quantummaid.mapmaid.builder.detection.serializedobject.fields.FieldDetector;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

import static de.quantummaid.mapmaid.builder.conventional.ConventionalDefinitionFactories.CUSTOM_PRIMITIVE_MAPPINGS;
import static de.quantummaid.mapmaid.builder.detection.SimpleDetector.detector;
import static de.quantummaid.mapmaid.builder.detection.customprimitive.deserialization.ConstructorBasedCustomPrimitiveDeserializationDetector.constructorBased;
import static de.quantummaid.mapmaid.builder.detection.customprimitive.deserialization.EnumCustomPrimitiveDeserializationDetector.enumDeserialization;
import static de.quantummaid.mapmaid.builder.detection.customprimitive.deserialization.StaticMethodBasedCustomPrimitiveDeserializationDetector.staticMethodBased;
import static de.quantummaid.mapmaid.builder.detection.customprimitive.serialization.EnumSerializationDetector.enumBased;
import static de.quantummaid.mapmaid.builder.detection.customprimitive.serialization.MethodNameBasedCustomPrimitiveSerializationDetector.methodBased;
import static de.quantummaid.mapmaid.shared.validators.NotNullValidator.validateNotNull;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class NewDetectorBuilder {
    private final List<FieldDetector> fieldDetectors;
    private final List<SerializedObjectDeserializationDetector> serializedObjectDeserializationDetectors;
    private final List<CustomPrimitiveSerializationDetector> customPrimitiveSerializationDetectors;
    private final List<CustomPrimitiveDeserializationDetector> customPrimitiveDeserializationDetectors;

    public static NewDetectorBuilder detectorBuilder() {
        final NewDetectorBuilder detectorBuilder = new NewDetectorBuilder(
                new ArrayList<>(10),
                new ArrayList<>(10),
                new ArrayList<>(10),
                new ArrayList<>(10)
        );
        return detectorBuilder;
    }

    public NewDetectorBuilder withCustomPrimitiveSerializerFactory(final CustomPrimitiveSerializationDetector detector) {
        validateNotNull(detector, "detector");
        this.customPrimitiveSerializationDetectors.add(detector);
        return this;
    }

    public NewDetectorBuilder withCustomPrimitiveDeserializerFactory(final CustomPrimitiveDeserializationDetector detector) {
        validateNotNull(detector, "detector");
        this.customPrimitiveDeserializationDetectors.add(detector);
        return this;
    }

    public NewDetectorBuilder withFieldDetector(final FieldDetector fieldDetector) {
        validateNotNull(fieldDetector, "fieldDetector");
        this.fieldDetectors.add(fieldDetector);
        return this;
    }

    public NewDetectorBuilder withSerializedObjectDeserializer(final SerializedObjectDeserializationDetector detector) {
        validateNotNull(detector, "detector");
        this.serializedObjectDeserializationDetectors.add(detector);
        return this;
    }

    public NewDetectorBuilder withFactoryAndConstructorBasedCustomPrimitiveFactory() {
        this.customPrimitiveSerializationDetectors.add(enumBased());
        this.customPrimitiveSerializationDetectors.add(methodBased(CUSTOM_PRIMITIVE_MAPPINGS));

        this.customPrimitiveDeserializationDetectors.add(enumDeserialization());
        this.customPrimitiveDeserializationDetectors.add(staticMethodBased(CUSTOM_PRIMITIVE_MAPPINGS));
        this.customPrimitiveDeserializationDetectors.add(constructorBased(CUSTOM_PRIMITIVE_MAPPINGS));

        return this;
    }

    public SimpleDetector build() {
        return detector(
                this.fieldDetectors,
                this.serializedObjectDeserializationDetectors,
                this.customPrimitiveSerializationDetectors,
                this.customPrimitiveDeserializationDetectors
        );
    }
}
