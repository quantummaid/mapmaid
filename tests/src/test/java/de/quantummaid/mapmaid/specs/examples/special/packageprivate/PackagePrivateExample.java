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

package de.quantummaid.mapmaid.specs.examples.special.packageprivate;

import org.junit.jupiter.api.Test;

import static de.quantummaid.mapmaid.builder.customtypes.DuplexType.customPrimitive;
import static de.quantummaid.mapmaid.specs.examples.special.packageprivate.PackagePrivateClass.packagePrivateClass;
import static de.quantummaid.mapmaid.specs.examples.system.ScenarioBuilder.scenarioBuilderFor;

public final class PackagePrivateExample {

    @Test
    public void packagePrivateExample() {
        scenarioBuilderFor(PackagePrivateClass.class)
                .withSerializedForm("\"foo\"")
                .withDeserializedForm(packagePrivateClass("foo"))
                .withSerializationFailing("type 'de.quantummaid.mapmaid.specs.examples.special.packageprivate.PackagePrivateClass' " +
                        "cannot be detected because it is not public (you can still register it manually)")
                .withDeserializationFailing("type 'de.quantummaid.mapmaid.specs.examples.special.packageprivate.PackagePrivateClass' " +
                        "cannot be detected because it is not public (you can still register it manually)")
                .withDuplexFailing("type 'de.quantummaid.mapmaid.specs.examples.special.packageprivate.PackagePrivateClass' " +
                        "cannot be detected because it is not public (you can still register it manually)")
                .withFixedScenarios((mapMaidBuilder, requiredCapabilities) -> mapMaidBuilder
                        .withCustomType(requiredCapabilities, customPrimitive(PackagePrivateClass.class, object -> "foo", PackagePrivateClass::packagePrivateClass)))
                .run();
    }
}
