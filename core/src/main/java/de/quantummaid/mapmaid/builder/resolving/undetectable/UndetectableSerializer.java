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

package de.quantummaid.mapmaid.builder.resolving.undetectable;

import de.quantummaid.mapmaid.builder.resolving.Context;
import de.quantummaid.mapmaid.builder.resolving.Report;
import de.quantummaid.mapmaid.builder.resolving.StatefulDefinition;
import de.quantummaid.mapmaid.builder.resolving.StatefulSerializer;
import de.quantummaid.mapmaid.builder.resolving.processing.CollectionResult;
import de.quantummaid.mapmaid.debug.scaninformation.ScanInformation;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import static de.quantummaid.mapmaid.builder.resolving.Report.failure;

@ToString
@EqualsAndHashCode(callSuper = true)
public final class UndetectableSerializer extends StatefulSerializer {
    private final String reason;

    private UndetectableSerializer(final Context context,
                                   final String reason) {
        super(context);
        this.reason = reason;
    }

    public static StatefulDefinition undetectableSerializer(final Context context,
                                                            final String reason) {
        return new UndetectableSerializer(context, reason);
    }

    @Override
    public Report getDefinition() {
        final ScanInformation scanInformation = this.context.scanInformationBuilder().build(null, null);
        final CollectionResult collectionResult = CollectionResult.collectionResult(null, scanInformation);
        return failure(collectionResult, "unable to detect serializer:\n" + this.reason);
    }
}
