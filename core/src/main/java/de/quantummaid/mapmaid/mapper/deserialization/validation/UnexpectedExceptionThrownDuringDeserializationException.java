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

package de.quantummaid.mapmaid.mapper.deserialization.validation;

import de.quantummaid.mapmaid.debug.MapMaidException;
import de.quantummaid.mapmaid.mapper.deserialization.DeserializationContext;

import static java.lang.String.valueOf;

public final class UnexpectedExceptionThrownDuringDeserializationException extends MapMaidException {
    public final Throwable unmappedException;
    public final String deserializerInput;
    public transient Object rawCompleteInput;

    private UnexpectedExceptionThrownDuringDeserializationException(
            final String msg,
            final Throwable unmappedException,
            final Object originalInput,
            final String deserializerInput) {
        super(msg, unmappedException);
        this.unmappedException = unmappedException;
        this.rawCompleteInput = originalInput;
        this.deserializerInput = deserializerInput;
    }

    static UnexpectedExceptionThrownDuringDeserializationException fromException(
            final DeserializationContext context,
            final String messageProvidingDebugInformation,
            final TrackingPosition position,
            final Throwable unmappedException,
            final Object input) {
        final String msg = "Unexpected exception thrown when deserializing field '" + position.render() +
                "': " + messageProvidingDebugInformation;
        return new UnexpectedExceptionThrownDuringDeserializationException(
                msg,
                unmappedException,
                context.getRawCompleteInput(),
                valueOf(input)
        );
    }
}
