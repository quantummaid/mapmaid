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

public final class InternalUnmarshallingException extends MapMaidException {
    public transient Object objectToUnmarshall;

    private InternalUnmarshallingException(final String message,
                                           final Exception cause,
                                           final Object objectToUnmarshall) {
        super(message, cause);
        this.objectToUnmarshall = objectToUnmarshall;
    }

    public static InternalUnmarshallingException internalUnmarshallingException(
            final Object input,
            final Exception cause
    ) {
        final String message = "Exception during unmarshalling: " + cause.getClass().getSimpleName();
        return new InternalUnmarshallingException(message, cause, input);
    }
}
