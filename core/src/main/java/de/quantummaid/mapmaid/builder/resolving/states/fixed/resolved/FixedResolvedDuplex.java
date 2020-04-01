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

package de.quantummaid.mapmaid.builder.resolving.states.fixed.resolved;

import de.quantummaid.mapmaid.builder.resolving.Context;
import de.quantummaid.mapmaid.builder.resolving.Report;
import de.quantummaid.mapmaid.builder.resolving.states.StatefulDefinition;
import de.quantummaid.mapmaid.builder.resolving.states.fixed.FixedDuplexDefinition;
import de.quantummaid.mapmaid.debug.ScanInformationBuilder;
import de.quantummaid.mapmaid.mapper.definitions.Definition;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.TypeDeserializer;
import de.quantummaid.mapmaid.mapper.serialization.serializers.TypeSerializer;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import static de.quantummaid.mapmaid.builder.resolving.Report.success;
import static de.quantummaid.mapmaid.builder.resolving.processing.CollectionResult.collectionResult;
import static de.quantummaid.mapmaid.mapper.definitions.GeneralDefinition.generalDefinition;

@ToString
@EqualsAndHashCode(callSuper = true)
public final class FixedResolvedDuplex extends FixedDuplexDefinition {

    private FixedResolvedDuplex(final Context context) {
        super(context);
    }

    public static StatefulDefinition fixedResolvedDuplex(final Context context) {
        return new FixedResolvedDuplex(context);
    }

    @Override
    public Report getDefinition() {
        final TypeSerializer serializer = this.context.serializer();
        final TypeDeserializer deserializer = this.context.deserializer();
        final ScanInformationBuilder scanInformationBuilder = this.context.scanInformationBuilder();
        scanInformationBuilder.setSerializer(serializer);
        scanInformationBuilder.setDeserializer(deserializer);
        final Definition definition = generalDefinition(this.context.type(), serializer, deserializer);
        return success(collectionResult(definition, scanInformationBuilder));
    }
}
