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

package de.quantummaid.mapmaid.builder.resolving.undetected;

import de.quantummaid.mapmaid.builder.contextlog.BuildContextLog;
import de.quantummaid.mapmaid.builder.detection.NewSimpleDetector;
import de.quantummaid.mapmaid.builder.resolving.Context;
import de.quantummaid.mapmaid.builder.resolving.StatefulDefinition;
import de.quantummaid.mapmaid.builder.resolving.StatefulSerializer;
import de.quantummaid.mapmaid.builder.resolving.resolving.ResolvingSerializer;
import de.quantummaid.mapmaid.mapper.serialization.serializers.TypeSerializer;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Optional;

import static de.quantummaid.mapmaid.builder.resolving.undetectable.UndetectableSerializer.undetectableSerializer;

@ToString
@EqualsAndHashCode
public final class UndetectedSerializer extends StatefulSerializer {

    private UndetectedSerializer(final Context context) {
        super(context);
    }

    public static UndetectedSerializer undetectedSerializer(final Context context) {
        return new UndetectedSerializer(context);
    }

    @Override
    public StatefulDefinition detect(final NewSimpleDetector detector, final BuildContextLog log) {
        final Optional<TypeSerializer> serializer = detector.detectSerializer(this.context.type(), log);
        if (serializer.isEmpty()) {
            return undetectableSerializer(this.context);
        }
        this.context.setSerializer(serializer.get());
        return ResolvingSerializer.resolvingSerializer(this.context);
    }
}
