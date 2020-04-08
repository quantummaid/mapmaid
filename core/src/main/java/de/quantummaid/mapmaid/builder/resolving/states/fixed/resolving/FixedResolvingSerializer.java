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

package de.quantummaid.mapmaid.builder.resolving.states.fixed.resolving;

import de.quantummaid.mapmaid.builder.resolving.Context;
import de.quantummaid.mapmaid.builder.resolving.states.StatefulDefinition;
import de.quantummaid.mapmaid.builder.resolving.states.fixed.FixedSerializerDefinition;
import de.quantummaid.mapmaid.builder.resolving.processing.Signal;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import static de.quantummaid.mapmaid.debug.Reason.becauseOf;
import static de.quantummaid.mapmaid.builder.resolving.states.fixed.resolved.FixedResolvedSerializer.fixedResolvedSerializer;

@ToString
@EqualsAndHashCode(callSuper = true)
public final class FixedResolvingSerializer extends FixedSerializerDefinition {

    private FixedResolvingSerializer(final Context context) {
        super(context);
    }

    public static StatefulDefinition fixedResolvingSerializer(final Context context) {
        return new FixedResolvingSerializer(context);
    }

    @Override
    public StatefulDefinition resolve() {
        this.context.serializer().requiredTypes().forEach(type ->
                this.context.dispatch(Signal.addSerialization(type, becauseOf(this.context.type()))));
        return fixedResolvedSerializer(this.context);
    }
}
