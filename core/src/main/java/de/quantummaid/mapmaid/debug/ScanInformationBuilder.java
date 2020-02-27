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

package de.quantummaid.mapmaid.debug;

import de.quantummaid.mapmaid.builder.resolving.Reason;
import de.quantummaid.mapmaid.debug.scaninformation.ScanInformation;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.TypeDeserializer;
import de.quantummaid.mapmaid.mapper.serialization.serializers.TypeSerializer;
import de.quantummaid.mapmaid.shared.types.ResolvedType;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static de.quantummaid.mapmaid.Collection.smallList;
import static de.quantummaid.mapmaid.Collection.smallMap;
import static de.quantummaid.mapmaid.debug.scaninformation.ActualScanInformation.actualScanInformation;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ScanInformationBuilder {
    private final ResolvedType type;
    private final List<Reason> deserializationReasons;
    private final List<Reason> serializationReasons;
    private final Map<TypeSerializer, List<String>> serializers;
    private final Map<TypeDeserializer, List<String>> deserializers;

    public static ScanInformationBuilder scanInformationBuilder(final ResolvedType type) {
        return new ScanInformationBuilder(
                type, smallList(), smallList(), smallMap(), smallMap());
    }

    public void addSerializer(final TypeSerializer serializer) {
        this.serializers.put(serializer, new ArrayList<>(1));
    }

    public void addDeserializer(final TypeDeserializer deserializer) {
        this.deserializers.put(deserializer, new ArrayList<>(1));
    }

    public void resetScan() {
        this.serializers.clear();
        this.deserializers.clear();
    }

    public void addSerializationReason(final Reason reason) {
        if (this.serializationReasons.contains(reason)) {
            return;
        }
        this.serializationReasons.add(reason);
    }

    public boolean removeSerializationReasonAndReturnIfEmpty(final Reason reason) {
        this.serializationReasons.remove(reason);
        return this.serializationReasons.isEmpty();
    }

    public void addDeserializationReason(final Reason reason) {
        if (this.deserializationReasons.contains(reason)) {
            return;
        }
        this.deserializationReasons.add(reason);
    }

    public boolean removeDeserializationReasonAndReturnIfEmpty(final Reason reason) {
        this.deserializationReasons.remove(reason);
        return this.deserializationReasons.isEmpty();
    }

    public void ignoreAllSerializers(final String reason) {
        this.serializers.values().forEach(reasons -> reasons.add(reason));
    }

    public void ignoreAllOtherSerializers(final TypeSerializer serializer,
                                          final String reason) {
        this.serializers.forEach((current, reasons) -> {
            if (!current.equals(serializer)) {
                reasons.add(reason);
            }
        });
    }

    public void ignoreAllDeserializers(final String reason) {
        this.deserializers.values().forEach(reasons -> reasons.add(reason));
    }

    public void ignoreAllOtherDeserializers(final TypeDeserializer deserializer,
                                            final String reason) {
        this.deserializers.forEach((current, reasons) -> {
            if (!current.equals(deserializer)) {
                reasons.add(reason);
            }
        });
    }

    public ScanInformation build(final TypeSerializer serializer, final TypeDeserializer deserializer) {
        return actualScanInformation(
                this.type,
                this.deserializationReasons,
                this.serializationReasons,
                serializer,
                deserializer,
                this.serializers,
                this.deserializers
        );
    }
}
