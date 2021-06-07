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

package de.quantummaid.mapmaid.polymorphy;

import de.quantummaid.mapmaid.debug.MapMaidException;
import de.quantummaid.mapmaid.debug.scaninformation.ScanInformation;
import de.quantummaid.mapmaid.mapper.universal.Universal;
import de.quantummaid.reflectmaid.typescanner.TypeIdentifier;

import static java.lang.String.format;

public final class MissingPolymorphicTypeFieldException extends MapMaidException {
    public final transient Object input;

    private MissingPolymorphicTypeFieldException(final String message,
                                                 final Object input) {
        super(message, null);
        this.input = input;
    }

    public static MissingPolymorphicTypeFieldException missingPolymorphicTypeFieldException(
            final Universal input,
            final TypeIdentifier typeIdentifier,
            final String typeField,
            final ScanInformation scanInformation
    ) {
        final String message = format(
                "Missing '%s' field to identify polymorphic subtype for type %s" +
                        "%n%n%s",
                typeField, typeIdentifier.description(), scanInformation.render()
        );
        return new MissingPolymorphicTypeFieldException(message, input.toNativeJava());
    }
}
