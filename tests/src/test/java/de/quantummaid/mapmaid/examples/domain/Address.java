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

package de.quantummaid.mapmaid.examples.domain;

import java.util.Objects;

import static java.lang.String.format;

@SuppressWarnings({"WeakerAccess", "ConstructorWithTooManyParameters"})
public final class Address {
    public final StreetName streetName;
    public final HouseNumber houseNumber;
    public final ZipCode zipCode;
    public final CityName city;
    public final Region region;
    public final Country country;

    private Address(
            final StreetName streetName,
            final HouseNumber houseNumber,
            final ZipCode zipCode,
            final CityName city,
            final Region region,
            final Country country) {
        this.streetName = streetName;
        this.houseNumber = houseNumber;
        this.zipCode = zipCode;
        this.city = city;
        this.region = region;
        this.country = country;
    }

    public static Address deserialize(
            final StreetName streetName,
            final HouseNumber houseNumber,
            final ZipCode zipCode,
            final CityName city,
            final Region region,
            final Country country) {
        if (Objects.isNull(streetName)) {
            throw new IllegalArgumentException("streetName must not be null");
        }
        if (Objects.isNull(houseNumber)) {
            throw new IllegalArgumentException("houseNumber must not be null");
        }
        if (Objects.isNull(zipCode)) {
            throw new IllegalArgumentException("zipCode must not be null");
        }
        if (Objects.isNull(city)) {
            throw new IllegalArgumentException("city must not be null");
        }
        if (Objects.isNull(region)) {
            throw new IllegalArgumentException("region must not be null");
        }
        if (Objects.isNull(country)) {
            throw new IllegalArgumentException("country must not be null");
        }
        return new Address(streetName, houseNumber, zipCode, city, region, country);
    }

    public String textual() {
        return format("%s %s - %s %s - %s - %s",
                this.streetName.stringValue(),
                this.houseNumber.stringValue(),
                this.zipCode.stringValue(),
                this.city.stringValue(),
                this.region.stringValue(),
                this.country.stringValue());
    }
}
