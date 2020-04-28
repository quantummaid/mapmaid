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

package de.quantummaid.mapmaid.builder.resolving.states;

import de.quantummaid.mapmaid.builder.detection.SimpleDetector;
import de.quantummaid.mapmaid.builder.resolving.Context;
import de.quantummaid.mapmaid.builder.resolving.Report;
import de.quantummaid.mapmaid.builder.resolving.disambiguator.Disambiguators;
import de.quantummaid.mapmaid.debug.Reason;
import de.quantummaid.mapmaid.shared.identifier.TypeIdentifier;

import java.util.List;
import java.util.Optional;

public abstract class StatefulDefinition {
    public final Context context;

    protected StatefulDefinition(final Context context) {
        this.context = context;
    }

    public abstract StatefulDefinition addSerialization(Reason reason);

    public abstract StatefulDefinition removeSerialization(Reason reason);

    public abstract StatefulDefinition addDeserialization(Reason reason);

    public abstract StatefulDefinition removeDeserialization(Reason reason);

    public TypeIdentifier type() {
        return this.context.type();
    }

    public boolean isInjection() {
        return false;
    }

    public StatefulDefinition detect(final SimpleDetector detector, // NOSONAR
                                     final Disambiguators disambiguators,
                                     final List<TypeIdentifier> injectedTypes) {
        return this;
    }

    public StatefulDefinition resolve() {
        return this;
    }

    public Optional<Report> getDefinition() {
        throw new UnsupportedOperationException(this.getClass() + " " + this.context.toString());
    }
}
