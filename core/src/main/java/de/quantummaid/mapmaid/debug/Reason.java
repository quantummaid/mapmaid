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

package de.quantummaid.mapmaid.debug;

import de.quantummaid.mapmaid.shared.identifier.TypeIdentifier;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Collection;
import java.util.List;

import static de.quantummaid.mapmaid.collections.Collection.smallList;
import static de.quantummaid.mapmaid.shared.validators.NotNullValidator.validateNotNull;
import static java.lang.String.format;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class Reason {
    private final String reason;
    private final TypeIdentifier parent;

    public static Reason reason(final String reason) {
        validateNotNull(reason, "reason");
        return new Reason(reason, null);
    }

    public static Reason manuallyAdded() {
        return reason("manually added");
    }

    public static Reason becauseOf(final TypeIdentifier parent) {
        return new Reason(format("because of %s", parent.description()), parent);
    }

    public List<String> render(final SubReasonProvider subReasonProvider) {
        return renderRecursive(subReasonProvider, smallList());
    }

    private List<String> renderRecursive(final SubReasonProvider subReasonProvider,
                                         final List<Reason> alreadyVisitedReasons) {
        if (this.parent == null) {
            return singletonList(this.reason);
        }
        final String parentName = this.parent.description();
        if (alreadyVisitedReasons.contains(this)) {
            return singletonList(format("%s...", parentName));
        }
        alreadyVisitedReasons.add(this);
        final List<Reason> parentReasons = subReasonProvider.reasonsFor(this.parent);
        return parentReasons.stream()
                .map(parentReason -> parentReason.renderRecursive(subReasonProvider, alreadyVisitedReasons))
                .flatMap(Collection::stream)
                .map(parentReason -> parentName + " -> " + parentReason)
                .collect(toList());
    }
}
