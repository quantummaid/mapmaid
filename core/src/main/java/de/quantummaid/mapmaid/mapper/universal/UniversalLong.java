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
import static java.lang.Long.parseLong;
import static java.lang.String.format;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class UniversalLong implements UniversalPrimitive {
    private final long value;

    public static UniversalLong universalLong(final long value) {
        return new UniversalLong(value);
    }

    public static UniversalLong universalLongFromUniversalDouble(final UniversalDouble universalDouble) {
        final Double o = (Double) universalDouble.toNativeJava();
        return universalLongFromDouble(o);
    }

    public static UniversalLong universalLongFromDouble(final Double value) {
        if (isNonFractionalDouble(value)) {
            return universalLong(value.longValue());
        } else {
            final String message = format("Cannot cast double '%s' to long", value);
            throw mapMaidException(message);
        }
    }

    public static UniversalLong universalLongFromUniversalString(final UniversalString universalString) {
        final String stringValue = (String) universalString.toNativeJava();
        return universalLongFromString(stringValue);
    }

    public static UniversalLong universalLongFromString(final String value) {
        final Long longValue = parseLong(value);
        return universalLong(longValue);
    }

    private static boolean isNonFractionalDouble(final Double value) {
        return Math.rint(value) == value;
    }

    @Override
    public Object toNativeJava() {
        return this.value;
    }

    public long toNativeLong() {
        return this.value;
    }

    public int toNativeIntExact() {
        //directly taken from the Math.integerExactInt method
        if ((int) this.value != this.value) {
            final String message = String.format("Overflow when converting long '%d' to int.", this.value);
            throw mapMaidException(message);
        }
        return (int) this.value;
    }

    public short toNativeShortExact() {
        //directly taken from the BigDecimal shortValueExact method
        if ((short) this.value != this.value) {
            final String message = String.format("Overflow when converting long '%d' to short.", this.value);
            throw mapMaidException(message);
        }
        return (short) this.value;
    }

    public byte toNativeByteExact() {
        //directly taken from the BigDecimal byteValueExact method
        if ((byte) this.value != this.value) {
            final String message = format("Overflow when converting long '%d' to byte.", this.value);
            throw mapMaidException(message);
        }
        return (byte) this.value;
    }
}
