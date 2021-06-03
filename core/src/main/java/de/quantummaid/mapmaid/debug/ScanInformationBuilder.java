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

package de.quantummaid.mapmaid.debug;

import de.quantummaid.mapmaid.builder.resolving.disambiguator.DisambiguationResult;
import de.quantummaid.mapmaid.debug.scaninformation.Reasons;
import de.quantummaid.mapmaid.debug.scaninformation.ScanInformation;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.TypeDeserializer;
import de.quantummaid.mapmaid.mapper.serialization.serializers.TypeSerializer;
import de.quantummaid.mapmaid.mapper.serialization.serializers.serializedobject.SerializationField;
import de.quantummaid.mapmaid.mapper.serialization.serializers.serializedobject.SerializedObjectSerializer;
import de.quantummaid.reflectmaid.typescanner.SubReasonProvider;
import de.quantummaid.reflectmaid.typescanner.TypeIdentifier;
import de.quantummaid.reflectmaid.typescanner.requirements.DetectionRequirements;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.Map;

import static de.quantummaid.mapmaid.builder.resolving.Requirements.DESERIALIZATION;
import static de.quantummaid.mapmaid.builder.resolving.Requirements.SERIALIZATION;
import static de.quantummaid.mapmaid.collections.Collection.smallList;
import static de.quantummaid.mapmaid.collections.Collection.smallMap;
import static de.quantummaid.mapmaid.debug.scaninformation.ActualScanInformation.actualScanInformation;
import static de.quantummaid.mapmaid.debug.scaninformation.Reasons.reasons;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ScanInformationBuilder {
    private final TypeIdentifier type;
    private final Map<TypeSerializer, List<String>> serializers;
    private final Map<SerializationField, List<String>> serializationFields;
    private final Map<TypeDeserializer, List<String>> deserializers;
    private TypeSerializer serializer;
    private TypeDeserializer deserializer;

    public static ScanInformationBuilder scanInformationBuilder(final TypeIdentifier type) {
        return new ScanInformationBuilder(type, smallMap(), smallMap(), smallMap());
    }

    public void addSerializer(final TypeSerializer serializer) {
        this.serializers.put(serializer, smallList());
    }

    public void addSerializationField(final SerializationField field) {
        this.serializationFields.put(field, smallList());
    }

    public void addDeserializer(final TypeDeserializer deserializer) {
        this.deserializers.put(deserializer, smallList());
    }

    public void resetScan() {
        this.serializers.clear();
        this.deserializers.clear();
    }

    public void ignoreAllOtherSerializers(final TypeSerializer serializer,
                                          final String reason) {
        this.serializers.forEach((current, reasons) -> {
            if (!current.equals(serializer)) {
                reasons.add(reason);
            }
        });
    }

    public void ignoreDeserializer(final TypeDeserializer deserializer,
                                   final String reason) {
        this.deserializers.get(deserializer).add(reason);
    }

    public void ignoreDeserializer(final TypeDeserializer deserializer,
                                   final List<String> reasons) {
        reasons.forEach(reason -> ignoreDeserializer(deserializer, reason));
    }

    public void ignoreSerializer(final TypeSerializer serializer,
                                 final String reason) {
        this.serializers.get(serializer).add(reason);
    }

    public void ignoreSerializer(final TypeSerializer serializer,
                                 final List<String> reasons) {
        reasons.forEach(reason -> ignoreSerializer(serializer, reason));
    }

    public void ignoreSerializationField(final SerializationField field,
                                         final List<String> reasons) {
        this.serializationFields.get(field).addAll(reasons);
    }

    public void ignoreSerializationField(final SerializationField field,
                                         final String reason) {
        this.serializationFields.get(field).add(reason);
    }

    public void ignoreAllOtherDeserializers(final TypeDeserializer deserializer,
                                            final String reason) {
        this.deserializers.forEach((current, reasons) -> {
            if (!current.equals(deserializer)) {
                reasons.add(reason);
            }
        });
    }

    public void ignore(final Object object,
                       final String reason) {
        if (object instanceof SerializedObjectSerializer) {
            final List<SerializationField> fields = ((SerializedObjectSerializer) object).fields().fields();
            fields.forEach(field -> ignoreSerializationField(field, reason));
        } else if (object instanceof TypeSerializer) {
            ignoreSerializer((TypeSerializer) object, reason);
        } else if (object instanceof TypeDeserializer) {
            ignoreDeserializer((TypeDeserializer) object, reason);
        } else if (object instanceof SerializationField) {
            ignoreSerializationField((SerializationField) object, reason);
        } else {
            throw new UnsupportedOperationException("This should never happen. Unknown object: " + object);
        }
    }

    public void setResult(final DisambiguationResult result,
                          final DetectionRequirements detectionRequirements) {
        if (detectionRequirements.requires(SERIALIZATION)) {
            serializer = result.serializer();
        }
        if (detectionRequirements.requires(DESERIALIZATION)) {
            deserializer = result.deserializer();
        }
    }

    public ScanInformation build(final SubReasonProvider serializationSubReasonProvider,
                                 final SubReasonProvider deserializationSubReasonProvider,
                                 final DetectionRequirements detectionRequirements,
                                 final DisambiguationResult disambiguationResult) {
        if (disambiguationResult != null) {
            serializer = disambiguationResult.serializer();
            deserializer = disambiguationResult.deserializer();
        }
        if (serializer != null) {
            if (serializer instanceof SerializedObjectSerializer) {
                final SerializedObjectSerializer serializedObjectSerializer =
                        (SerializedObjectSerializer) serializer;
                serializedObjectSerializer.fields().fields().forEach(serializationFields::remove);
            } else {
                this.serializers.remove(serializer);
            }
        }
        if (this.deserializer != null) {
            this.deserializers.remove(deserializer);
        }
        final Reasons reasons = reasons(
                detectionRequirements.reasonsFor(DESERIALIZATION),
                detectionRequirements.reasonsFor(SERIALIZATION),
                serializationSubReasonProvider,
                deserializationSubReasonProvider);
        return actualScanInformation(
                type,
                reasons,
                serializer,
                deserializer,
                serializers,
                serializationFields,
                deserializers
        );
    }
}
