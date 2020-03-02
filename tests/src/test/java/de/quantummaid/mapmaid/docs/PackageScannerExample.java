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

package de.quantummaid.mapmaid.docs;

import com.google.gson.Gson;
import de.quantummaid.mapmaid.MapMaid;
import de.quantummaid.mapmaid.builder.scanning.DefaultPackageScanner;
import org.junit.jupiter.api.Test;

import java.util.List;

public final class PackageScannerExample {
    private static final String PACKAGE_TO_SCAN_1 = "gfrgr"; // TODO
    private static final String PACKAGE_TO_SCAN_2 = "vrgr"; // TODO

    private static final String THE_PACKAGE_NAMES_TO_SCAN_RECURSIVELY = "feef"; // TODO
    private static final Class<?> THE_LIST_OF_CLASSES_TO_INCLUDE = Object.class; // TODO
    private static final String THE_PACKAGE_NAMES_TO_BLACKLIST_RECURSIVELY = "grge"; // TODO
    private static final Class<?> THE_LIST_OF_CLASSES_TO_EXCLUDE = Object.class; // TODO

    @Test
    public void apiExample() {
        final Gson gson = new Gson();

        //Showcase start config
        MapMaid.aMapMaid(PACKAGE_TO_SCAN_1, PACKAGE_TO_SCAN_2 /* etc.*/)
                /* further configuration */
                .build();
        //Showcase end config

        //Showcase start api
        MapMaid.aMapMaid(DefaultPackageScanner.defaultPackageScanner(
                List.of(THE_PACKAGE_NAMES_TO_SCAN_RECURSIVELY), // TODO
                List.of(THE_LIST_OF_CLASSES_TO_INCLUDE), // TODO
                List.of(THE_PACKAGE_NAMES_TO_BLACKLIST_RECURSIVELY), // TODO
                List.of(THE_LIST_OF_CLASSES_TO_EXCLUDE)) // TODO
        )
                .usingJsonMarshaller(gson::toJson, gson::fromJson)
                .build();
        //Showcase end api
    }
}
