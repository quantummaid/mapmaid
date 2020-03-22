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

package de.quantummaid.mapmaid.docs.examples.special.local_class;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.junit.jupiter.api.Test;

import static de.quantummaid.mapmaid.builder.customtypes.DuplexType.customPrimitive;
import static de.quantummaid.mapmaid.docs.examples.system.ScenarioBuilder.scenarioBuilderFor;

public final class LocalClassExample {

    @Test
    public void localClassExample() {
        @ToString
        @EqualsAndHashCode
        final class LocalClass {
            private final String value;

            public LocalClass(final String value) {
                this.value = value;
            }

            public String value() {
                return this.value;
            }
        }

        scenarioBuilderFor(LocalClass.class)
                .withSerializedForm("\"foo\"")
                .withDeserializedForm(new LocalClass("foo"))
                .withSerializationFailing("type 'de.quantummaid.mapmaid.docs.examples.special.local_class.LocalClassExample$1LocalClass' " +
                        "cannot be detected because it is a local class (you can still register it manually)")
                .withDeserializationFailing("type 'de.quantummaid.mapmaid.docs.examples.special.local_class.LocalClassExample$1LocalClass' " +
                        "cannot be detected because it is a local class (you can still register it manually)")
                .withDuplexFailing("type 'de.quantummaid.mapmaid.docs.examples.special.local_class.LocalClassExample$1LocalClass' " +
                        "cannot be detected because it is a local class (you can still register it manually)")
                .withFixedScenarios((mapMaidBuilder, requiredCapabilities) -> mapMaidBuilder
                        .withCustomType(requiredCapabilities, customPrimitive(LocalClass.class, object -> "foo", LocalClass::new)))
                .run();
    }
}
