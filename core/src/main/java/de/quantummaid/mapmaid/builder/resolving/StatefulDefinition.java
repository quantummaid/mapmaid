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

import de.quantummaid.mapmaid.builder.detection.SimpleDetector;
import de.quantummaid.mapmaid.builder.resolving.disambiguator.Disambiguators;
import de.quantummaid.mapmaid.shared.types.ResolvedType;

import static de.quantummaid.mapmaid.mapper.definitions.validation.statemachine.proxy.StateProxy.createStateProxyIfNotAlready;

public abstract class StatefulDefinition {
    public final Context context;

    protected StatefulDefinition(final Context context) {
        this.context = context;
    }

    // TODO
    public void onEnter() {
    }

    public abstract StatefulDefinition addSerialization(Reason reason);

    public abstract StatefulDefinition removeSerialization(Reason reason);

    public abstract StatefulDefinition addDeserialization(Reason reason);

    public abstract StatefulDefinition removeDeserialization(Reason reason);

    static StatefulDefinition statefulDefinition(final StatefulDefinition statefulDefinition) {
        return createStateProxyIfNotAlready(StatefulDefinition.class, statefulDefinition, StatefulDefinition::onEnter);
    }

    public ResolvedType type() {
        return this.context.type();
    }

    public StatefulDefinition detect(final SimpleDetector detector,
                                     final Disambiguators disambiguators) {
        return this;
    }

    public StatefulDefinition resolve() {
        return this;
    }

    public Report getDefinition() {
        throw new UnsupportedOperationException(this.getClass() + " " + this.context.toString());
    }
}
