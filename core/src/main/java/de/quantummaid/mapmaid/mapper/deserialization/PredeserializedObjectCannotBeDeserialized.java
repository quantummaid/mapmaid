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

package de.quantummaid.mapmaid.mapper.deserialization;

import de.quantummaid.mapmaid.debug.MapMaidException;
import de.quantummaid.mapmaid.debug.scaninformation.ScanInformation;

import static java.lang.String.format;

public final class PredeserializedObjectCannotBeDeserialized extends MapMaidException {
    private final transient Object objectToSerialize;

    private PredeserializedObjectCannotBeDeserialized(final String message,
                                                      final Object objectToSerialize) {
        super(message, null);
        this.objectToSerialize = objectToSerialize;
    }

    public static PredeserializedObjectCannotBeDeserialized predeserializedObjectCannotBeDeserialized(
            final ScanInformation scanInformation,
            final Object objectToSerialize
    ) {
        final String message = format(
                "Pre-deserialized objects are not supported for deserialization'. " +
                        "Please use injections to add pre-deserialized objects." +
                        "%n%n%s",
                scanInformation.render()
        );
        return new PredeserializedObjectCannotBeDeserialized(message, objectToSerialize);
    }

    public Object objectToSerialize() {
        return objectToSerialize;
    }
}
