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

package de.quantummaid.mapmaid.mapper.serialization.universalmerger;

import de.quantummaid.mapmaid.mapper.universal.Universal;
import de.quantummaid.mapmaid.mapper.universal.UniversalObject;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static de.quantummaid.mapmaid.debug.MapMaidException.mapMaidException;
import static de.quantummaid.mapmaid.mapper.serialization.universalmerger.FieldNormalizer.determineField;

public final class UniversalMerger {

    private UniversalMerger() {
    }

    public static Universal mergeUniversal(final Universal target, final List<Universal> inputs) {
        if (inputs.isEmpty()) {
            return target;
        }
        final UniversalObject objectTarget = castToUniversalObject(target);
        final Map<String, Universal> targetMap = new LinkedHashMap<>(objectTarget.toUniversalMap());
        for (final Universal input : inputs) {
            mergeUniversal(targetMap, input);
        }
        return UniversalObject.universalObject(targetMap);
    }

    private static void mergeUniversal(final Map<String, Universal> target, final Universal input) {
        final UniversalObject objectInput = castToUniversalObject(input);
        final Set<String> existingKeys = target.keySet();
        objectInput.toUniversalMap().forEach((key, universal) -> {
            final String normalizedKey = determineField(key, existingKeys);
            target.put(normalizedKey, universal);
        });
    }

    private static UniversalObject castToUniversalObject(final Universal universal) {
        if (!(universal instanceof UniversalObject)) {
            throw mapMaidException("can only merge universal objects but found " + universal.getClass());
        }
        return (UniversalObject) universal;
    }
}
