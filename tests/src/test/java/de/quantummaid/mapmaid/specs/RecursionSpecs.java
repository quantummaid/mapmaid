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

package de.quantummaid.mapmaid.specs;

import de.quantummaid.mapmaid.domain.*;
import org.junit.jupiter.api.Test;

import static de.quantummaid.mapmaid.MapMaid.aMapMaid;
import static de.quantummaid.mapmaid.testsupport.givenwhenthen.Given.given;

public final class RecursionSpecs {

    @Test
    public void recursionRecipeCanDetectCustomPrimitives() {
        given(
                aMapMaid().serializingAndDeserializing(AString.class).build()
        )
                .when().theDefinitionsAreQueried()
                .theDefinitionsContainExactlyTheCustomPrimitives(AString.class)
                .theDefinitionsContainExactlyTheSerializedObjects();
    }

    @Test
    public void recursionRecipeCanDetectRequiredTypesForSerializedObjects() {
        given(
                aMapMaid().serializingAndDeserializing(AComplexNestedValidatedType.class).build()
        )
                .when().theDefinitionsAreQueried()
                .theDefinitionsContainExactlyTheSerializedObjects(AComplexNestedValidatedType.class, AComplexValidatedType.class)
                .theDefinitionsContainExactlyTheCustomPrimitives(AValidatedString.class);
    }

    @Test
    public void recursionRecipeCanDetectThroughCollections() {
        given(
                aMapMaid().serializingAndDeserializing(AComplexTypeWithCollections.class).build()
        )
                .when().theDefinitionsAreQueried()
                .theDefinitionsContainExactlyTheSerializedObjects(AComplexTypeWithCollections.class)
                .theDefinitionsContainExactlyTheCustomPrimitives(AString.class, ANumber.class);
    }

    @Test
    public void recursionRecipeCanAutoloadPrimitiveDataTypes() {
        given(
                aMapMaid().serializingAndDeserializing(AComplexNestedValidatedType.class).build()
        )
                .when().theDefinitionsAreQueried()
                .theDefinitionsContainExactlyTheCustomPrimitives(AValidatedString.class)
                .theDefinitionsContainExactlyTheSerializedObjects(AComplexNestedValidatedType.class, AComplexValidatedType.class);
    }
}
