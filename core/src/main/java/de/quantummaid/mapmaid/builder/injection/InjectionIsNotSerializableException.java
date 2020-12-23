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

package de.quantummaid.mapmaid.builder.injection;

import de.quantummaid.mapmaid.debug.MapMaidException;
import de.quantummaid.mapmaid.debug.scaninformation.ScanInformation;
import de.quantummaid.mapmaid.shared.identifier.TypeIdentifier;

import static java.lang.String.format;

public final class InjectionIsNotSerializableException extends MapMaidException {
    public transient Object objectToSerialize;

    private InjectionIsNotSerializableException(final String message,
                                                final Object objectToSerialize) {
        super(message, null);
        this.objectToSerialize = objectToSerialize;
    }

    public static InjectionIsNotSerializableException injectionIsNotSerializableException(
            final TypeIdentifier typeIdentifier,
            final ScanInformation scanInformation,
            final Object objectToSerialize
    ) {
        final String message = format("Tried to serialize type '%s' that is marked as injection-only%n%n%s",
                typeIdentifier.description(),
                scanInformation.render()
        );
        return new InjectionIsNotSerializableException(message, objectToSerialize);
    }
}
