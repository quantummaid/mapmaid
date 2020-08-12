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

package de.quantummaid.mapmaid.specs.examples.customprimitives.success.serialization_only;

import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.GregorianCalendar;

import static de.quantummaid.mapmaid.specs.examples.customprimitives.success.serialization_only.DateFormatted.fromDate;
import static de.quantummaid.mapmaid.specs.examples.system.ScenarioBuilder.scenarioBuilderFor;

public final class SerializationOnlyExample {

    @Test
    public void serializationOnlyExample() {
        scenarioBuilderFor(DateFormatted.class)
                .withSerializedForm("\"01-01-2010\"")
                .withDeserializedForm(fromDate(new GregorianCalendar(2010, Calendar.JANUARY, 1).getTime()))
                .withSerializationSuccessful()
                .withDeserializationFailing("java.util.Date: unable to detect deserialization-only:\n" +
                        "no deserialization detected:\n" +
                        "[Native java classes cannot be detected]")
                .withDuplexFailing()
                .run();
    }
}
