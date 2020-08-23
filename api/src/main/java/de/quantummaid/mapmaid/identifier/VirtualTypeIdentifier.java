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

package de.quantummaid.mapmaid.identifier;

import de.quantummaid.reflectmaid.ResolvedType;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import static de.quantummaid.reflectmaid.validators.NotNullValidator.validateNotNull;
import static java.lang.String.format;
import static java.util.UUID.randomUUID;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class VirtualTypeIdentifier implements TypeIdentifier {
    private final String id;

    public static TypeIdentifier virtualTypeIdentifier(final String id) {
        validateNotNull(id, "id");
        return new VirtualTypeIdentifier(id);
    }

    public static TypeIdentifier uniqueVirtualTypeIdentifier() {
        final String id = randomUUID().toString();
        return new VirtualTypeIdentifier(id);
    }

    @Override
    public boolean isVirtual() {
        return true;
    }

    @Override
    public ResolvedType getRealType() {
        throw new UnsupportedOperationException(format("Virtual type '%s' does not have a real type", description()));
    }

    @Override
    public String description() {
        return format("<virtual type '%s'>", this.id);
    }
}
