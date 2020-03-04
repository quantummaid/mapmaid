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

package de.quantummaid.mapmaid.docs.examples.system;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Optional;

import static java.util.Optional.ofNullable;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class Result {
    private Throwable initializationException;
    private Throwable serializationException;
    private Throwable deserializationException;
    private String serializationResult;
    private Object deserializationResult;

    public static Result emptyResult() {
        return new Result();
    }

    public Result withInitializationException(final Throwable exception) {
        this.initializationException = exception;
        return this;
    }

    public Optional<Throwable> initializationException() {
        return ofNullable(this.initializationException);
    }

    public Result withSerializationException(final Throwable exception) {
        this.serializationException = exception;
        return this;
    }

    public Optional<Throwable> serializationException() {
        return ofNullable(this.serializationException);
    }

    public Result withDeserializationException(final Throwable exception) {
        this.deserializationException = exception;
        return this;
    }

    public Optional<Throwable> deserializationException() {
        return ofNullable(this.deserializationException);
    }

    public Result withSerializationResult(final String serializationResult) {
        this.serializationResult = serializationResult;
        return this;
    }

    public Optional<String> serializationResult() {
        return ofNullable(this.serializationResult);
    }

    public Result withDeserializationResult(final Object deserializationResult) {
        this.deserializationResult = deserializationResult;
        return this;
    }

    public Optional<Object> deserializationResult() {
        return ofNullable(this.deserializationResult);
    }
}
