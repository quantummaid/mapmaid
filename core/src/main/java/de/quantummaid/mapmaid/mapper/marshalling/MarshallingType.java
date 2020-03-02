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

package de.quantummaid.mapmaid.mapper.marshalling;

import de.quantummaid.mapmaid.shared.validators.RequiredStringValidator;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class MarshallingType {
    public static final MarshallingType JSON = json();
    public static final MarshallingType XML = xml();
    public static final MarshallingType YAML = yaml();

    private final String type;

    public static MarshallingType marshallingType(final String type) {
        RequiredStringValidator.validateNotNullNorEmpty(type, "type");
        return new MarshallingType(type);
    }

    public static MarshallingType json() {
        return marshallingType("json");
    }

    public static MarshallingType xml() {
        return marshallingType("xml");
    }

    public static MarshallingType yaml() {
        return marshallingType("yaml");
    }

    public String internalValueForMapping() {
        return this.type;
    }
}
