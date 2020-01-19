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

package de.quantummaid.mapmaid.builder.resolving;

import de.quantummaid.mapmaid.mapper.deserialization.Deserializer;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.TypeDeserializer;
import de.quantummaid.mapmaid.mapper.serialization.Serializer;
import de.quantummaid.mapmaid.mapper.serialization.serializers.TypeSerializer;
import de.quantummaid.mapmaid.shared.types.ResolvedType;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.LinkedList;
import java.util.List;

import static de.quantummaid.mapmaid.builder.resolving.Reason.becauseOf;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class Context {
    private final Processor processor;
    private final ResolvedType type;
    private final List<Reason> deserializationReasons;
    private final List<Reason> serializationReasons;
    private TypeSerializer serializer;
    private TypeDeserializer deserializer;

    public static Context emptyContext(final Processor processor,
                                       final ResolvedType type) {
        return new Context(processor, type, new LinkedList<>(), new LinkedList<>());
    }

    public ResolvedType type() {
        return this.type;
    }

    public Processor processor() {
        return this.processor;
    }

    public void addSerializationReason(final Reason reason) {
        if (this.serializationReasons.contains(reason)) {
            return;
        }
        this.serializationReasons.add(reason);
    }

    public boolean removeSerializationReasonAndReturnIfEmpty(final Reason reason) {
        this.serializationReasons.remove(reason);
        if (this.serializationReasons.isEmpty()) {
            final Reason transitiveReason = becauseOf(this.type);
            this.processor.dispatch(definition -> definition.removeSerialization(transitiveReason));
            return true;
        }
        return false;
    }

    public void addDeserializationReason(final Reason reason) {
        if (this.deserializationReasons.contains(reason)) {
            return;
        }
        this.deserializationReasons.add(reason);
    }

    public boolean removeDeserializationReasonAndReturnIfEmpty(final Reason reason) {
        this.deserializationReasons.remove(reason);
        if (this.deserializationReasons.isEmpty()) {
            final Reason transitiveReason = becauseOf(this.type);
            this.processor.dispatch(definition -> definition.removeDeserialization(transitiveReason));
            return true;
        }
        return false;
    }

    public TypeSerializer serializer() {
        return this.serializer;
    }

    public void setSerializer(final TypeSerializer serializer) {
        this.serializer = serializer;
    }

    public TypeDeserializer deserializer() {
        return this.deserializer;
    }

    public void setDeserializer(final TypeDeserializer deserializer) {
        this.deserializer = deserializer;
    }
}
