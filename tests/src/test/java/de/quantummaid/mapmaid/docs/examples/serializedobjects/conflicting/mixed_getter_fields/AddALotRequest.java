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

package de.quantummaid.mapmaid.docs.examples.serializedobjects.conflicting.mixed_getter_fields;

import de.quantummaid.mapmaid.docs.examples.customprimitives.success.deserialization_only.example2.StreetName;
import de.quantummaid.mapmaid.docs.examples.customprimitives.success.normal.example2.TownName;
import de.quantummaid.mapmaid.docs.examples.customprimitives.success.factory_by_classname.example1.CountryName;
import de.quantummaid.mapmaid.docs.examples.customprimitives.success.multiple_static_factories.example1.RegionName;
import lombok.*;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
public final class AddALotRequest {
    @Setter
    @Getter
    private StreetName streetName;

    @Setter
    @Getter
    private TownName townName;

    @Setter
    @Getter
    private RegionName regionName;

    @Setter // TODO public felder setzten nicht impl in doku
    public CountryName countryName;
}
