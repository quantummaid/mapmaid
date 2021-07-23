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

package de.quantummaid.mapmaid.mapper.universal;

import de.quantummaid.mapmaid.shared.validators.NotNullValidator;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import static de.quantummaid.mapmaid.debug.MapMaidException.mapMaidException;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class UniversalString implements UniversalPrimitive {
    private final String value;

    public static UniversalString universalString(final String value) {
        NotNullValidator.validateNotNull(value, "value");
        return new UniversalString(value);
    }

    public static UniversalString universalStringFromUniversalDouble(final UniversalDouble universalDouble) {
        final Double doubleValue = (Double) universalDouble.toNativeJava();
        return universalStringFromDouble(doubleValue);
    }

    public static UniversalString universalStringFromDouble(final Double doubleValue) {
        return universalString(doubleValue.toString());
    }

    public static UniversalString universalStringFromUniversalLong(final UniversalLong universalLong) {
        final Long longValue = (Long) universalLong.toNativeJava();
        return universalStringFromLong(longValue);
    }

    public static UniversalString universalStringFromLong(final Long longValue) {
        return universalString(longValue.toString());
    }

    public static UniversalString universalStringFromUniversalBoolean(final UniversalBoolean universalBoolean) {
        final Boolean booleanValue = (Boolean) universalBoolean.toNativeJava();
        return universalStringFromBoolean(booleanValue);
    }

    public static UniversalString universalStringFromBoolean(final Boolean booleanValue) {
        return universalString(booleanValue.toString());
    }

    public static UniversalString universalStringFromCharacter(final Character characterValue) {
        return universalString(characterValue.toString());
    }

    @Override
    public Object toNativeJava() {
        return this.value;
    }

    public String toNativeStringValue() {
        return this.value;
    }

    public Character toNativeCharacterValue() {
        final char[] charArray = value.toCharArray();
        if (charArray.length != 1) {
            throw mapMaidException("cannot convert string '" + value + "' to a character");
        }
        return charArray[0];
    }
}
