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

package de.quantummaid.mapmaid.debug.scaninformation;

import de.quantummaid.mapmaid.debug.Reason;
import de.quantummaid.mapmaid.debug.SubReasonProvider;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Collection;
import java.util.List;

import static java.lang.String.format;
import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.joining;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class Reasons {
    private final List<Reason> deserializationReasons;
    private final List<Reason> serializationReasons;
    private final SubReasonProvider serializationSubReasonProvider;
    private final SubReasonProvider deserializationSubReasonProvider;

    public static Reasons reasons(final List<Reason> deserializationReasons,
                                  final List<Reason> serializationReasons,
                                  final SubReasonProvider serializationSubReasonProvider,
                                  final SubReasonProvider deserializationSubReasonProvider) {
        return new Reasons(
                deserializationReasons,
                serializationReasons,
                serializationSubReasonProvider,
                deserializationSubReasonProvider
        );
    }

    public String dumpSerializationReasons() {
        return this.serializationReasons.stream()
                .map(reason -> reason.render(this.serializationSubReasonProvider))
                .flatMap(Collection::stream)
                .map(reason -> format("\t- %s", reason))
                .collect(joining("\n", "", "\n"));
    }

    public String dumpDerializationReasons() {
        return this.deserializationReasons.stream()
                .map(reason -> reason.render(this.deserializationSubReasonProvider))
                .flatMap(Collection::stream)
                .map(reason -> format("\t- %s", reason))
                .collect(joining("\n", "", "\n"));
    }

    public boolean hasSerializationReasons() {
        return !this.serializationReasons.isEmpty();
    }

    public boolean hasDeserializationReasons() {
        return !this.deserializationReasons.isEmpty();
    }

    public List<Reason> serializationReasons() {
        return unmodifiableList(this.serializationReasons);
    }

    public List<Reason> deserializationReasons() {
        return unmodifiableList(this.deserializationReasons);
    }
}
