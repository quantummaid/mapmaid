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

package de.quantummaid.mapmaid.specs.rootcause;

import de.quantummaid.mapmaid.specs.rootcause.cyclic.LevelA;
import de.quantummaid.mapmaid.specs.rootcause.normal.Level1;
import de.quantummaid.mapmaid.specs.rootcause.wildcardusecase.UseCaseWithWildcardInReturnType;
import de.quantummaid.reflectmaid.GenericType;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static de.quantummaid.mapmaid.MapMaid.aMapMaid;
import static de.quantummaid.mapmaid.builder.recipes.scanner.ClassScannerRecipe.addAllReferencedClassesIn;
import static de.quantummaid.mapmaid.testsupport.givenwhenthen.Given.given;
import static de.quantummaid.reflectmaid.GenericType.fromResolvedType;
import static de.quantummaid.reflectmaid.GenericType.genericType;
import static de.quantummaid.reflectmaid.WildcardedType.wildcardType;

public final class RootCauseSpecs {

    @Test
    public void rootCauseForRegistrationIsProvided() {
        given(
                () -> aMapMaid()
                        .serializingAndDeserializing(Level1.class)
                        .build()
        )
                .when().mapMaidIsInstantiated()
                .anExceptionIsThrownWithAMessageContainingLine("- de.quantummaid.mapmaid.specs.rootcause.normal.Level2 -> de.quantummaid.mapmaid.specs.rootcause.normal.Level1 -> manually added");
    }

    @Test
    public void rootCauseForRegistrationIsProvidedForCyclicTypes() {
        given(
                () -> aMapMaid()
                        .serializingAndDeserializing(LevelA.class)
                        .build()
        )
                .when().mapMaidIsInstantiated()
                .anExceptionIsThrownWithAMessageContainingLine("- de.quantummaid.mapmaid.specs.rootcause.cyclic.LevelB -> de.quantummaid.mapmaid.specs.rootcause.cyclic.LevelA -> manually added")
                .anExceptionIsThrownWithAMessageContainingLine(
                        "- de.quantummaid.mapmaid.specs.rootcause.cyclic.LevelB -> de.quantummaid.mapmaid.specs.rootcause.cyclic.LevelA -> de.quantummaid.mapmaid.specs.rootcause.cyclic.LevelB...");
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void rootCauseForRegistrationIsProvidedForWildcard() {
        final GenericType<List> nestedTypeWithWildcard = genericType(List.class, genericType(ArrayList.class, genericType(LinkedList.class, fromResolvedType(wildcardType()))));
        given(
                () -> aMapMaid()
                        .serializingAndDeserializing(nestedTypeWithWildcard)
                        .build()
        )
                .when().mapMaidIsInstantiated()
                .anExceptionIsThrownWithAMessageContainingLine("- java.util.LinkedList<?> -> java.util.ArrayList<java.util.LinkedList<?>> -> java.util.List<java.util.ArrayList<java.util.LinkedList<?>>> -> manually added");
    }

    @Test
    public void rootCauseForUseCaseWithWildcardInReturnType() {
        given(
                () -> aMapMaid()
                        .usingRecipe(addAllReferencedClassesIn(UseCaseWithWildcardInReturnType.class))
                        .build()
        )
                .when().mapMaidIsInstantiated()
                .anExceptionIsThrownWithAMessageContainingLine("- java.util.List<?> -> because return type of method 'List method()' " +
                        "[public java.util.List<?> de.quantummaid.mapmaid.specs.rootcause.wildcardusecase.UseCaseWithWildcardInReturnType.method()]");
    }
}
