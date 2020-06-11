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

package de.quantummaid.mapmaid;

import de.quantummaid.mapmaid.builder.MarshallerAutoloadingException;
import de.quantummaid.mapmaid.jackson.JacksonJsonMarshaller;
import de.quantummaid.mapmaid.minimaljson.MinimalJsonMarshaller;
import de.quantummaid.mapmaid.testsupport.givenwhenthen.Given;
import org.junit.jupiter.api.Test;

import static de.quantummaid.mapmaid.mapper.marshalling.MarshallingType.JSON;

public class AutoloadingSpecs {

    @Test
    public void throwsAnErrorWhenMultipleJsonImplementationsAreFoundWithoutHint() {
        Given.given(
                () -> MapMaid.aMapMaid().build()
        )
                .when().mapMaidIsInstantiated()
                .anExceptionIsThrownWithType(MarshallerAutoloadingException.class)
                .anExceptionIsThrownWithAMessageContaining("conflicting implementations for marshalling type 'json'")
                .anExceptionIsThrownWithAMessageContaining(JacksonJsonMarshaller.class.getName())
                .anExceptionIsThrownWithAMessageContaining(MinimalJsonMarshaller.class.getName());
    }

    @Test
    public void doesNotAutoloadWhenCustomMarshallersAreConfigured() {
        Given.given(
                () -> MapMaid.aMapMaid()
                        .withAdvancedSettings(advancedBuilder ->
                                advancedBuilder.usingJsonMarshaller(
                                        object -> "customMarshalledValue", input -> unsupported()
                                ))
                        .build()
        )
                .when().mapMaidMarshalsFromUniversalObject(null, JSON)
                .theSerializationResultWas("customMarshalledValue");
    }

    private <T> T unsupported() {
        throw new UnsupportedOperationException();
    }

    // Can I add multiple lines in the services

    //     public void complainsWhenNoJsonImplementationsIsFound() {
    //     public void autoloadsSingleJsonImplementation() {
    // non-json
    //     public void autoloadsSingleYamlImplementation() {
    //     public void autoloadsSingleXmlImplementation() {
}
