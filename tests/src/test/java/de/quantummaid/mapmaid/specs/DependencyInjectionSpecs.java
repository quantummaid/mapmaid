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

import de.quantummaid.mapmaid.MapMaid;
import de.quantummaid.mapmaid.builder.recipes.di.GeneralDependencyInjector;
import de.quantummaid.mapmaid.testsupport.domain.valid.AComplexType;
import de.quantummaid.mapmaid.testsupport.domain.valid.ANumber;
import de.quantummaid.mapmaid.testsupport.domain.valid.AString;
import de.quantummaid.mapmaid.testsupport.givenwhenthen.Given;
import de.quantummaid.mapmaid.testsupport.givenwhenthen.Marshallers;
import de.quantummaid.mapmaid.testsupport.givenwhenthen.Unmarshallers;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static de.quantummaid.mapmaid.builder.recipes.di.DiRecipe.toUseDependencyInjectionWith;
import static de.quantummaid.mapmaid.mapper.marshalling.MarshallingType.json;

public final class DependencyInjectionSpecs {

    @SuppressWarnings("AnonymousInnerClassMayBeStatic")
    @Test
    public void aClassCanBeDeserializedUsingDependencyInjection() {
        Given.given(
                MapMaid.aMapMaid()
                        .withManuallyAddedType(AComplexType.class)
                        .usingRecipe(toUseDependencyInjectionWith(new GeneralDependencyInjector() {
                            @SuppressWarnings("unchecked")
                            @Override
                            public <T> T getInstance(final Class<T> type) {
                                return (T) ANumber.fromInt(42);
                            }
                        }, ANumber.class))
                        .usingJsonMarshaller(Marshallers.jsonMarshaller(), Unmarshallers.jsonUnmarshaller())
                        .build()
        )
                .when().mapMaidDeserializes("" +
                "{\n" +
                "   \"stringA\": \"a\",\n" +
                "   \"stringB\": \"b\",\n" +
                "   \"number1\": \"1\",\n" +
                "   \"number2\": \"1\"\n" +
                "}")
                .from(json()).toTheType(AComplexType.class)
                .noExceptionHasBeenThrown()
                .theDeserializedObjectIs(AComplexType.deserialize(
                        AString.fromStringValue("a"),
                        AString.fromStringValue("b"),
                        ANumber.fromInt(42),
                        ANumber.fromInt(42)
                ));
    }

    @SuppressWarnings("AnonymousInnerClassMayBeStatic")
    @Disabled
    @Test
    public void aClassCanBeDeserializedUsingDependencyInjectionEvenIfTheCorrespondingFieldIsNotPresent() {
        Given.given(
                MapMaid.aMapMaid()
                        .withManuallyAddedType(AComplexType.class)
                        .usingRecipe(toUseDependencyInjectionWith(new GeneralDependencyInjector() {
                            @SuppressWarnings("unchecked")
                            @Override
                            public <T> T getInstance(final Class<T> type) {
                                return (T) ANumber.fromInt(42);
                            }
                        }, ANumber.class))
                        .usingJsonMarshaller(Marshallers.jsonMarshaller(), Unmarshallers.jsonUnmarshaller())
                        .build()
        )
                .when().mapMaidDeserializes("" +
                "{\n" +
                "   \"stringA\": \"a\",\n" +
                "   \"stringB\": \"b\"\n" +
                "}")
                .from(json()).toTheType(AComplexType.class)
                .noExceptionHasBeenThrown()
                .theDeserializedObjectIs(AComplexType.deserialize(
                        AString.fromStringValue("a"),
                        AString.fromStringValue("b"),
                        ANumber.fromInt(42),
                        ANumber.fromInt(42)
                ));
    }
}
