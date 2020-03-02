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

package de.quantummaid.mapmaid.builder.conventional;

import de.quantummaid.mapmaid.builder.detection.Detector;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import static de.quantummaid.mapmaid.builder.conventional.DetectorBuilder.detectorBuilder;

@ToString
@EqualsAndHashCode
public final class ConventionalDetectors {
    private static final String[] DEFAULT_CLASS_PATTERNS = {
            ".*DTO",
            ".*Dto",
            ".*Request",
            ".*Response",
            ".*State",
    };

    private ConventionalDetectors() {
    }

    public static Detector conventionalDetector() {
        return conventionalDetector("stringValue",
                "fromStringValue",
                "deserialize",
                DEFAULT_CLASS_PATTERNS
        );
    }

    public static Detector conventionalDetector(final String customPrimitiveSerializationMethodName,
                                                final String customPrimitiveDeserializationMethodName,
                                                final String serializedObjectDeserializationMethodName,
                                                final String... serializedObjectNameDetectionPatterns) {
        return detectorBuilder()
                .withNameAndConstructorBasedCustomPrimitiveFactory(customPrimitiveSerializationMethodName, customPrimitiveDeserializationMethodName)
                .withSerializedObjectFactory(ConventionalDefinitionFactories.pojoSerializedObjectFactory())
                .withSerializedObjectFactory(ConventionalDefinitionFactories.allSerializedObjectFactory(serializedObjectDeserializationMethodName))
                .build();
    }

    public static Detector conventionalDetectorWithAnnotations() {
        return conventionalDetectorWithAnnotations("stringValue",
                "fromStringValue",
                "deserialize",
                DEFAULT_CLASS_PATTERNS
        );
    }

    public static Detector conventionalDetectorWithAnnotations(
            final String customPrimitiveSerializationMethodName,
            final String customPrimitiveDeserializationMethodName,
            final String serializedObjectDeserializationMethodName,
            final String... serializedObjectNameDetectionPatterns) {
        return detectorBuilder()
                .withCustomPrimitiveFactory(ConventionalDefinitionFactories.customPrimitiveClassAnnotationFactory())
                .withCustomPrimitiveFactory(ConventionalDefinitionFactories.customPrimitiveMethodAnnotationFactory())
                .withNameAndConstructorBasedCustomPrimitiveFactory(customPrimitiveSerializationMethodName, customPrimitiveDeserializationMethodName)
                .withSerializedObjectFactory(ConventionalDefinitionFactories.allSerializedObjectFactory(serializedObjectDeserializationMethodName))
                .build();
    }
}
