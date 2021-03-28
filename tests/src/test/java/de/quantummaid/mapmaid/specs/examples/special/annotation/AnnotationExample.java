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

package de.quantummaid.mapmaid.specs.examples.special.annotation;

import org.junit.jupiter.api.Test;

import static de.quantummaid.mapmaid.specs.examples.system.ScenarioBuilder.scenarioBuilderFor;

public final class AnnotationExample {

    @Test
    public void annotationExample() {
        final Annotation instance = new Annotation() {
            @Override
            public String value() {
                return "foo";
            }

            @Override
            public Class<? extends java.lang.annotation.Annotation> annotationType() {
                return Annotation.class;
            }
        };
        scenarioBuilderFor(Annotation.class)
                .withSerializedForm("\"foo\"")
                .withDeserializedForm(instance)
                .withSerializationFailing("type 'de.quantummaid.mapmaid.specs.examples.special.annotation.Annotation' " +
                        "cannot be detected because it is an annotation (you can still register it manually)")
                .withDeserializationFailing("type 'de.quantummaid.mapmaid.specs.examples.special.annotation.Annotation' " +
                        "cannot be detected because it is an annotation (you can still register it manually)")
                .withDuplexFailing("type 'de.quantummaid.mapmaid.specs.examples.special.annotation.Annotation' " +
                        "cannot be detected because it is an annotation (you can still register it manually)")
                .withManual((mapMaidBuilder, requiredCapabilities) -> {
                    if (requiredCapabilities.hasDeserialization() && requiredCapabilities.hasSerialization()) {
                        mapMaidBuilder.serializingAndDeserializingCustomPrimitive(Annotation.class, object -> "foo", value -> instance);
                    } else if (requiredCapabilities.hasSerialization()) {
                        mapMaidBuilder.serializingCustomPrimitive(Annotation.class, object -> "foo");
                    } else if (requiredCapabilities.hasDeserialization()) {
                        mapMaidBuilder.deserializingCustomPrimitive(Annotation.class, value -> instance);
                    }
                })
                .run();
    }
}
