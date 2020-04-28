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

package de.quantummaid.mapmaid.builder.resolving.states.undetectable;

import de.quantummaid.mapmaid.builder.resolving.Context;
import de.quantummaid.mapmaid.builder.resolving.Report;
import de.quantummaid.mapmaid.builder.resolving.processing.CollectionResult;
import de.quantummaid.mapmaid.builder.resolving.states.StatefulDefinition;
import de.quantummaid.mapmaid.builder.resolving.states.StatefulDuplex;
import de.quantummaid.mapmaid.debug.ScanInformationBuilder;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Optional;

import static de.quantummaid.mapmaid.builder.resolving.Report.failure;

@ToString
@EqualsAndHashCode(callSuper = true)
public final class UndetectableDuplex extends StatefulDuplex {
    private final String reason;

    private UndetectableDuplex(final Context context,
                               final String reason) {
        super(context);
        this.reason = reason;
    }

    public static StatefulDefinition undetectableDuplex(final Context context,
                                                        final String reason) {
        return new UndetectableDuplex(context, reason);
    }

    @Override
    public Optional<Report> getDefinition() {
        final ScanInformationBuilder scanInformationBuilder = this.context.scanInformationBuilder();
        final CollectionResult collectionResult = CollectionResult.collectionResult(null, scanInformationBuilder);
        return Optional.of(failure(collectionResult, "unable to detect duplex:\n" + this.reason));
    }
}
