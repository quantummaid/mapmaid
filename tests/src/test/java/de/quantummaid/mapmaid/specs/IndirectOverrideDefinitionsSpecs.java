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

package de.quantummaid.mapmaid.specs;

import de.quantummaid.mapmaid.MapMaid;
import de.quantummaid.mapmaid.mapper.deserialization.DeserializationFields;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.serializedobjects.SerializedObjectDeserializer;
import de.quantummaid.mapmaid.mapper.serialization.serializers.serializedobject.SerializationField;
import de.quantummaid.mapmaid.mapper.serialization.serializers.serializedobject.SerializationFields;
import de.quantummaid.mapmaid.testsupport.domain.valid.AComplexNestedType;
import de.quantummaid.mapmaid.testsupport.domain.valid.AComplexType;
import de.quantummaid.mapmaid.testsupport.domain.valid.ANumber;
import de.quantummaid.mapmaid.testsupport.domain.valid.AString;
import de.quantummaid.mapmaid.testsupport.givenwhenthen.Given;
import de.quantummaid.mapmaid.testsupport.givenwhenthen.Marshallers;
import de.quantummaid.mapmaid.testsupport.givenwhenthen.Unmarshallers;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static de.quantummaid.mapmaid.mapper.definitions.GeneralDefinition.generalDefinition;
import static de.quantummaid.mapmaid.mapper.deserialization.deserializers.customprimitives.CustomPrimitiveDeserializer.constantDeserializer;
import static de.quantummaid.mapmaid.mapper.marshalling.MarshallingType.json;
import static de.quantummaid.mapmaid.mapper.serialization.serializers.customprimitives.CustomPrimitiveSerializer.constantSerializer;
import static de.quantummaid.mapmaid.shared.types.ClassType.fromClassWithoutGenerics;

public final class IndirectOverrideDefinitionsSpecs {

    @Test
    public void customDeserializationForCustomPrimitiveOverridesIndirectDefault() {
        Given.given(
                MapMaid.aMapMaid()
                        .mapping(AComplexType.class)
                        .withManuallyAddedDefinition(generalDefinition(
                                fromClassWithoutGenerics(ANumber.class),
                                constantSerializer("23"),
                                constantDeserializer(ANumber.fromInt(23))
                        ))
                        .withAdvancedSettings(advancedBuilder -> {
                            advancedBuilder.usingJsonMarshaller(Marshallers.jsonMarshaller(), Unmarshallers.jsonUnmarshaller());
                        })
                        .build()
        )
                .when().mapMaidDeserializes("42").from(json()).toTheType(ANumber.class)
                .noExceptionHasBeenThrown()
                .theDeserializedObjectIs(ANumber.fromInt(23));
    }

    // TODO refactor
    @Test
    public void customDeserializationForSerializedObjectOverridesIndirectDefault() {
        Given.given(
                MapMaid.aMapMaid()
                        .mapping(AComplexNestedType.class)
                        .withManuallyAddedDefinition(generalDefinition(
                                fromClassWithoutGenerics(AComplexType.class),
                                de.quantummaid.mapmaid.mapper.serialization.serializers.serializedobject.SerializedObjectSerializer.serializedObjectSerializer(SerializationFields.serializationFields(
                                        List.of(SerializationField.serializationField(fromClassWithoutGenerics(AString.class), "foo", object -> AString.fromStringValue("bar")))
                                )),
                                new SerializedObjectDeserializer() {
                                    @Override
                                    public Object deserialize(final Map<String, Object> elements) throws Exception {
                                        return AComplexType.deserialize(AString.fromStringValue("custom1"), AString.fromStringValue("custom2"), ANumber.fromInt(100), ANumber.fromInt(200));
                                    }

                                    @Override
                                    public DeserializationFields fields() {
                                        return DeserializationFields.deserializationFields(Map.of("foo", fromClassWithoutGenerics(AString.class)));
                                    }

                                    @Override
                                    public String description() {
                                        throw new UnsupportedOperationException(); // TODO
                                    }
                                }
                        ))
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingJsonMarshaller(Marshallers.jsonMarshaller(), Unmarshallers.jsonUnmarshaller()))
                        .build()
        )
                .when().mapMaidDeserializes("{\"foo\": \"qwer\"}").from(json()).toTheType(AComplexType.class)
                .noExceptionHasBeenThrown()
                .theDeserializedObjectIs(AComplexType.deserialize(AString.fromStringValue("custom1"), AString.fromStringValue("custom2"), ANumber.fromInt(100), ANumber.fromInt(200)));
    }
}
