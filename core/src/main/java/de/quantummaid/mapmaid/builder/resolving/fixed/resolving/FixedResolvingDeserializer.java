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

package de.quantummaid.mapmaid.builder.resolving.fixed.resolving;

import de.quantummaid.mapmaid.builder.resolving.Context;
import de.quantummaid.mapmaid.builder.resolving.StatefulDefinition;
import de.quantummaid.mapmaid.builder.resolving.fixed.FixedDeserializerDefinition;
import de.quantummaid.mapmaid.builder.resolving.processing.Signal;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import static de.quantummaid.mapmaid.builder.resolving.Reason.becauseOf;
import static de.quantummaid.mapmaid.builder.resolving.fixed.resolved.FixedResolvedDeserializer.fixedResolvedDeserializer;

@ToString
@EqualsAndHashCode(callSuper = true)
public final class FixedResolvingDeserializer extends FixedDeserializerDefinition {

    private FixedResolvingDeserializer(final Context context) {
        super(context);
    }

    public static StatefulDefinition fixedResolvingDeserializer(final Context context) {
        return new FixedResolvingDeserializer(context);
    }

    @Override
    public StatefulDefinition resolve() {
        this.context.deserializer().requiredTypes().forEach(requirement ->
                this.context.dispatch(
                        Signal.addDeserialization(requirement, becauseOf(this.context.type()))));
        return fixedResolvedDeserializer(this.context);
    }
}
