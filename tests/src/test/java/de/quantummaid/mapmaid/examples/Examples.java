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

package de.quantummaid.mapmaid.examples;

import com.google.gson.Gson;
import de.quantummaid.mapmaid.MapMaid;
import de.quantummaid.mapmaid.examples.domain.*;
import de.quantummaid.mapmaid.mapper.deserialization.validation.AggregatedValidationException;
import org.junit.jupiter.api.Test;

@SuppressWarnings("ALL")
public final class Examples {
    private static final Gson GSON = new Gson();
    private MapMaid mapMaid;

    static MapMaid get() {
        return MapMaid.aMapMaid("de.quantummaid.mapmaid.examples.domain")
                .withExceptionIndicatingValidationError(IllegalArgumentException.class)
                .usingJsonMarshaller(GSON::toJson, GSON::fromJson)
                .build();
    }

    @Test
    public void example_serializing() {
        this.mapMaid = get();
        final Person queen = Person.deserialize(
                FullName.deserialize(
                        FirstName.fromStringValue("Beatrix"),
                        LastName.fromStringValue("Armgard"),
                        LastNamePrefix.fromStringValue("van"),
                        new MiddleName[]{MiddleName.fromStringValue("Wilhelmina")}
                ),
                Address.deserialize(
                        StreetName.fromStringValue("Dam"),
                        HouseNumber.fromStringValue("1"),
                        ZipCode.fromStringValue("1012KB"),
                        CityName.fromStringValue("Amsterdam"),
                        Region.fromStringValue("Noord-Holland"),
                        Country.fromStringValue("Netherlands")
                ));

        final String result = this.mapMaid.serializeToJson(queen);
        System.out.println(result);
    }

    @Test
    public void example_deserializing() {
        this.mapMaid = get();
        final String json = "{\"fullName\":{" +
                "\"middleNames\":[\"Wilhelmina\", \"Petronella\"]," +
                "\"firstName\":\"Beatrix\"," +
                "\"lastNamePrefix\":\"van\"," +
                "\"lastName\":\"Armgard\"}," +
                "\"address\":{" +
                "\"houseNumber\":\"1\"," +
                "\"zipCode\":\"1012KB\"," +
                "\"country\":\"Netherlands\"," +
                "\"streetName\":\"Dam\"," +
                "\"region\":\"Noord-Holland\"," +
                "\"city\":\"Amsterdam\"}}";

        final Person queen = this.mapMaid.deserializeJson(json, Person.class);

        System.out.println(queen.fullName.textual());
        System.out.println(queen.address.textual());
    }

    @Test
    public void example_deserializingWithValidations() {
        this.mapMaid = get();
        final String json = "{\"fullName\":{" +
                "\"firstName\":\"Be\"}," +
                "\"address\":{" +
                "\"houseNumber\":\"1\"," +
                "\"zipCode\":\"1012KB\"," +
                "\"country\":\"Netherlands\"," +
                "\"streetName\":\"Da\"," +
                "\"region\":\"Noord-Holland\"," +
                "\"city\":\"Amsterdam\"}}";

        try {
            this.mapMaid.deserializeJson(json, ValidPerson.class);
        } catch (AggregatedValidationException e) {
            System.out.println(e.getValidationErrors());
        }
    }
}
