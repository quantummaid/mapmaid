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

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import static de.quantummaid.mapmaid.debug.MapMaidException.mapMaidException;
import static java.lang.Double.parseDouble;
import static java.lang.String.format;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class UniversalDouble implements UniversalPrimitive {
    private final double value;

    public static UniversalDouble universalDouble(final double value) {
        return new UniversalDouble(value);
    }

    public static UniversalDouble universalDoubleFromUniversalString(final UniversalString universalString) {
        final String stringValue = (String) universalString.toNativeJava();
        return universalDoubleFromString(stringValue);
    }

    public static UniversalDouble universalDoubleFromFloat(final float floatValue) {
        return universalDoubleFromString(Float.toString(floatValue));
    }

    private static UniversalDouble universalDoubleFromString(final String stringValue) {
        final Double doubleValue = parseDouble(stringValue);
        return universalDouble(doubleValue);
    }

    public static UniversalDouble universalDoubleFromUniversalLong(final UniversalLong universalLong) {
        final Long longValue = (Long) universalLong.toNativeJava();
        return universalDoubleFromLong(longValue);
    }

    private static UniversalDouble universalDoubleFromLong(final Long longValue) {
        final double value = longValue.doubleValue();
        return universalDouble(value);
    }

    @Override
    public Object toNativeJava() {
        return this.value;
    }

    public float toNativeFloatExact() {
        final float floatValue = (float) this.value;
        final Float wrappedFloat = floatValue;
        final Double wrappedDouble = this.value;
        final String floatStringRepresentation = wrappedFloat.toString();
        final String doubleStringRepresentation = wrappedDouble.toString();
        if (!floatStringRepresentation.equals(doubleStringRepresentation)) {
            final String message = format("Overflow when converting double '%s' to float.", this.value);
            throw mapMaidException(message);
        }
        return floatValue;
    }

    public double toNativeDouble() {
        return this.value;
    }
}
