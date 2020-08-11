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

package de.quantummaid.mapmaid.builder.resolving.states.fixed;

import de.quantummaid.mapmaid.builder.resolving.Context;
import de.quantummaid.mapmaid.builder.resolving.requirements.DetectionRequirements;
import de.quantummaid.mapmaid.builder.resolving.requirements.RequirementsReducer;
import de.quantummaid.mapmaid.builder.resolving.states.StatefulDefinition;
import de.quantummaid.mapmaid.debug.Reason;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.TypeDeserializer;
import de.quantummaid.mapmaid.mapper.serialization.serializers.TypeSerializer;
import de.quantummaid.mapmaid.shared.identifier.TypeIdentifier;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

import static de.quantummaid.mapmaid.builder.resolving.processing.Signal.*;
import static de.quantummaid.mapmaid.builder.resolving.states.fixed.Resolved.fixedResolvedDuplex;
import static de.quantummaid.mapmaid.debug.Reason.becauseOf;

@ToString
@EqualsAndHashCode(callSuper = true)
public final class Resolving extends StatefulDefinition {

    private Resolving(final Context context) {
        super(context);
    }

    public static StatefulDefinition fixedResolvingDuplex(final Context context) {
        return new Resolving(context);
    }

    @Override
    public StatefulDefinition changeRequirements(final RequirementsReducer reducer) {
        this.context.scanInformationBuilder().changeRequirements(reducer);
        return this;
    }

    @Override
    public StatefulDefinition resolve() {
        final DetectionRequirements detectionRequirements = context.scanInformationBuilder().detectionRequirements();
        final Reason reason = becauseOf(this.context.type());
        if (detectionRequirements.serialization) {
            final TypeSerializer serializer = context.serializer().orElseThrow();
            final List<TypeIdentifier> requiredTypes = serializer.requiredTypes();
            requiredTypes.forEach(type -> context.dispatch(addSerialization(type, reason)));
            if (serializer.forcesDependenciesToBeObjects()) {
                requiredTypes.forEach(type -> context.dispatch(forceObject(reason)));
            }
        }
        if (detectionRequirements.deserialization) {
            final TypeDeserializer deserializer = context.deserializer().orElseThrow();
            final List<TypeIdentifier> requiredTypes = deserializer.requiredTypes();
            requiredTypes.forEach(type -> context.dispatch(addDeserialization(type, reason)));
            if (deserializer.forcesDependenciesToBeObjects()) {
                requiredTypes.forEach(type -> context.dispatch(forceObject(reason)));
            }
        }
        return fixedResolvedDuplex(this.context);
    }
}
