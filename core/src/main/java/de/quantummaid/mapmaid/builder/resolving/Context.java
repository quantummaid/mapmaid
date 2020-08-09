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

package de.quantummaid.mapmaid.builder.resolving;

import de.quantummaid.mapmaid.builder.resolving.processing.Signal;
import de.quantummaid.mapmaid.debug.Reason;
import de.quantummaid.mapmaid.debug.ScanInformationBuilder;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.TypeDeserializer;
import de.quantummaid.mapmaid.mapper.serialization.serializers.TypeSerializer;
import de.quantummaid.mapmaid.shared.identifier.TypeIdentifier;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Optional;
import java.util.function.Consumer;

import static de.quantummaid.mapmaid.debug.Reason.becauseOf;
import static java.util.Optional.ofNullable;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class Context {
    private final Consumer<Signal> dispatcher;
    private final TypeIdentifier type;
    private TypeSerializer serializer;
    private TypeDeserializer deserializer;
    private final ScanInformationBuilder scanInformationBuilder;

    public static Context emptyContext(final Consumer<Signal> dispatcher,
                                       final TypeIdentifier type) {
        return new Context(dispatcher, type, ScanInformationBuilder.scanInformationBuilder(type));
    }

    public TypeIdentifier type() {
        return this.type;
    }

    public void dispatch(final Signal signal) {
        System.out.println("signal = " + signal);
        this.dispatcher.accept(signal);
    }

    public boolean removeSerializationReasonAndReturnIfEmpty(final Reason reason) {
        final boolean empty = this.scanInformationBuilder.removeSerializationReasonAndReturnIfEmpty(reason);
        if (empty) {
            final Reason transitiveReason = becauseOf(this.type);
            dispatch(definition -> definition.changeRequirements(current -> current.removeSerialization(transitiveReason)));
            return true;
        }
        return false;
    }

    public boolean removeDeserializationReasonAndReturnIfEmpty(final Reason reason) {
        final boolean empty = this.scanInformationBuilder.removeDeserializationReasonAndReturnIfEmpty(reason);
        if (empty) {
            final Reason transitiveReason = becauseOf(this.type);
            dispatch(definition -> definition.changeRequirements(current -> current.removeDeserialization(transitiveReason)));
            return true;
        }
        return false;
    }

    public ScanInformationBuilder scanInformationBuilder() {
        return this.scanInformationBuilder;
    }

    public Optional<TypeSerializer> serializer() {
        return ofNullable(this.serializer);
    }

    public void setSerializer(final TypeSerializer serializer) {
        this.serializer = serializer;
    }

    public Optional<TypeDeserializer> deserializer() {
        return ofNullable(this.deserializer);
    }

    public void setDeserializer(final TypeDeserializer deserializer) {
        this.deserializer = deserializer;
    }
}
