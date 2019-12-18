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

package de.quantummaid.mapmaid.testsupport.domain.scannable;

import de.quantummaid.mapmaid.testsupport.domain.exceptions.AnException;

import java.io.Serializable;
import java.util.Objects;

public final class AScannableNumber implements Serializable {
    public static final int MAX_VALUE = 50;
    private final int value;

    private AScannableNumber(final int value) {
        this.value = value;
    }

    public static AScannableNumber fromInt(final int value) {
        return new AScannableNumber(value);
    }

    public static AScannableNumber fromString(final String value) {
        final Integer integer = Integer.valueOf(value);
        if (integer > MAX_VALUE) {
            throw AnException.anException("value cannot be over 50");
        }
        return new AScannableNumber(integer);
    }

    public String internalValueForMapping() {
        return String.valueOf(this.value);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final AScannableNumber aNumber = (AScannableNumber) o;
        return this.value == aNumber.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.value);
    }

    @Override
    public String toString() {
        return "AScannableNumber{" +
                "value=" + this.value +
                '}';
    }

    public boolean isLowerThen(final int value) {
        return this.value < value;
    }
}
