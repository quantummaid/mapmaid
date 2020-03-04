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

package de.quantummaid.mapmaid.testsupport.givenwhenthen;

import de.quantummaid.mapmaid.MapMaid;
import de.quantummaid.mapmaid.builder.recipes.marshallers.urlencoded.UrlEncodedMarshallerRecipe;
import de.quantummaid.mapmaid.testsupport.domain.exceptions.AnException;
import de.quantummaid.mapmaid.testsupport.domain.valid.*;

public final class MapMaidInstances {
    private MapMaidInstances() {
    }

    public static MapMaid theExampleMapMaidWithAllMarshallers() {
        final MapMaid mapMaid = MapMaid.aMapMaid()
                .withAdvancedSettings(advancedBuilder -> {
                    advancedBuilder
                            .usingJsonMarshaller(Marshallers.jsonMarshaller(), Unmarshallers.jsonUnmarshaller())
                            .usingXmlMarshaller(Marshallers.xmlMarshaller(), Unmarshallers.xmlUnmarshaller())
                            .usingYamlMarshaller(Marshallers.yamlMarshaller(), Unmarshallers.yamlUnmarshaller());
                })
                .usingRecipe(UrlEncodedMarshallerRecipe.urlEncodedMarshaller())
                .serializingAndDeserializing(AComplexType.class)
                .serializingAndDeserializing(AComplexTypeWithValidations.class)
                .serializingAndDeserializing(AComplexTypeWithArray.class)
                .serializingAndDeserializing(AComplexTypeWithListButArrayConstructor.class)
                .serializingAndDeserializing(AComplexNestedValidatedType.class)
                .serializingAndDeserializing(AComplexTypeWithNestedCollections.class)
                .serializingAndDeserializing(AComplexTypeWithCollections.class)
                .serializingAndDeserializing(AComplexNestedType.class)
                .serializingAndDeserializing(ACyclicType.class)
                .withExceptionIndicatingValidationError(AnException.class)
                .build();
        return mapMaid;
    }
}
