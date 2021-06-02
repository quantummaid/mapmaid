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

package de.quantummaid.mapmaid.builder.resolving.framework.processing;

import de.quantummaid.mapmaid.builder.resolving.framework.requirements.DetectionRequirementReasons;
import de.quantummaid.mapmaid.debug.ScanInformationBuilder;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class CollectionResult<T> {
    private final T definition;
    private final ScanInformationBuilder scanInformationBuilder;
    private final DetectionRequirementReasons detectionRequirements;

    public static <T> CollectionResult<T> collectionResult(final T definition,
                                                           final ScanInformationBuilder scanInformationBuilder,
                                                           final DetectionRequirementReasons detectionRequirements) {
        return new CollectionResult<>(definition, scanInformationBuilder, detectionRequirements);
    }

    public T definition() {
        return this.definition;
    }

    public ScanInformationBuilder scanInformation() {
        return this.scanInformationBuilder;
    }

    public DetectionRequirementReasons detectionRequirements() {
        return detectionRequirements;
    }
}
