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

import de.quantummaid.mapmaid.builder.detection.SimpleDetector;
import de.quantummaid.mapmaid.builder.detection.serializedobject.deserialization.StaticMethodDeserializationDetector;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import static de.quantummaid.mapmaid.builder.conventional.NewDetectorBuilder.detectorBuilder;
import static de.quantummaid.mapmaid.builder.detection.serializedobject.deserialization.ConstructorBasedDeserializationDetector.constructorBased;
import static de.quantummaid.mapmaid.builder.detection.serializedobject.deserialization.SetterBasedDeserializationDetector.setterBasedDeserializationDetector;
import static de.quantummaid.mapmaid.builder.detection.serializedobject.fields.GetterFieldDetector.getterFieldDetector;
import static de.quantummaid.mapmaid.builder.detection.serializedobject.fields.ModifierFieldDetector.modifierBased;

@ToString
@EqualsAndHashCode
public final class ConventionalDetectors {

    private ConventionalDetectors() {
    }

    public static SimpleDetector conventionalDetector() {
        return detectorBuilder()
                .withFactoryAndConstructorBasedCustomPrimitiveFactory()
                .withFieldDetector(getterFieldDetector()).withSerializedObjectDeserializer(setterBasedDeserializationDetector())
                .withFieldDetector(modifierBased())
                .withSerializedObjectDeserializer(StaticMethodDeserializationDetector.staticMethodBased())
                .withSerializedObjectDeserializer(constructorBased())
                .build();
    }
}
