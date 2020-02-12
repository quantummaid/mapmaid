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

package de.quantummaid.mapmaid.docs.examples.customprimitives.conflicting.type_variable_with_different_name;

import de.quantummaid.mapmaid.shared.types.ClassType;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static de.quantummaid.mapmaid.builder.resolving.disambiguator.fixed.builder.customprimitive.CustomPrimitiveBuilder.customPrimitive;
import static de.quantummaid.mapmaid.docs.examples.customprimitives.conflicting.type_variable_with_different_name.Street.street;
import static de.quantummaid.mapmaid.docs.examples.system.ScenarioBuilder.scenarioBuilderFor;
import static de.quantummaid.mapmaid.shared.types.ClassType.fromClassWithGenerics;
import static de.quantummaid.mapmaid.shared.types.ResolvedType.resolvedType;
import static de.quantummaid.mapmaid.shared.types.TypeVariableName.typeVariableName;

public final class TypeVariableWithDifferentNameExample {

    @Test
    public void typeVariableWithDifferentNameExample() {
        final ClassType resolvedType = fromClassWithGenerics(Street.class, Map.of(typeVariableName("T"), resolvedType(Object.class)));
        scenarioBuilderFor(resolvedType)
                .withDeserializedForm(street("foo"))
                .withSerializedForm("\"foo\"")
                .withAllScenariosFailing("ede.quantummaid.mapmaid.docs.examples.customprimitives.conflicting.type_variable_with_different_name.Street<java.lang.Object>: unable to detect", (mapMaidBuilder, capabilities) -> mapMaidBuilder
                        .withManuallyAddedDefinition(customPrimitive(resolvedType,
                                object -> ((Street<Object>) object).stringValue(),
                                Street::street), capabilities)) // TODO usage
                .run();
    }
}
