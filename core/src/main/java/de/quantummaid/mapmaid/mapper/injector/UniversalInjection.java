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

package de.quantummaid.mapmaid.mapper.injector;

import de.quantummaid.mapmaid.mapper.universal.Universal;
import de.quantummaid.mapmaid.shared.validators.NotNullValidator;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class UniversalInjection {
    private final PropertyName propertyName;
    private final Universal value;

    static UniversalInjection universalInjection(final PropertyName propertyName, final Universal value) {
        NotNullValidator.validateNotNull(propertyName, "propertyName");
        NotNullValidator.validateNotNull(value, "value");
        return new UniversalInjection(propertyName, value);
    }

    public PropertyName propertyName() {
        return this.propertyName;
    }

    public Universal value() {
        return this.value;
    }
}
