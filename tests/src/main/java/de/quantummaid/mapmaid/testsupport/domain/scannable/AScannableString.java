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

package de.quantummaid.mapmaid.testsupport.domain.scannable;

import de.quantummaid.mapmaid.testsupport.domain.exceptions.AnException;

import java.io.Serializable;
import java.util.Objects;

public final class AScannableString implements Serializable {
    private final String value;

    private AScannableString(final String value) {
        this.value = value;
    }

    public static AScannableString fromString(final String value) {
        if (value.contains("~")) {
            throw AnException.anException("value must not contain '~'");
        }
        return new AScannableString(value);
    }

    public String internalValueForMapping() {
        return this.value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final AScannableString aScannableString = (AScannableString) o;
        return Objects.equals(this.value, aScannableString.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.value);
    }

    @Override
    public String toString() {
        return "AScannableString{" +
                "value='" + this.value + '\'' +
                '}';
    }

    public boolean isEmpty() {
        return this.value.isEmpty();
    }
}
