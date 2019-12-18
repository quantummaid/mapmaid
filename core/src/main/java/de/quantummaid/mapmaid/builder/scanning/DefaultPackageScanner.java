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

package de.quantummaid.mapmaid.builder.scanning;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;

import static de.quantummaid.mapmaid.shared.validators.NotNullValidator.validateNotNull;
import static de.quantummaid.mapmaid.shared.validators.NotNullValidator.validateNotNullOrEmpty;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class DefaultPackageScanner implements PackageScanner {
    public final List<String> whitelistPackages;
    public final List<Class<?>> whitelistClasses;
    public final List<String> blacklistPackages;
    public final List<Class<?>> blacklistClasses;

    public static PackageScanner defaultPackageScanner(final List<String> whitelistPackages) {
        return defaultPackageScanner(whitelistPackages,
                List.of(),
                List.of(),
                List.of());
    }

    public static PackageScanner defaultPackageScanner(final List<String> whitelistPackages,
                                                       final List<Class<?>> whitelistClasses,
                                                       final List<String> blacklistPackages,
                                                       final List<Class<?>> blacklistClasses) {
        validateNotNullOrEmpty(whitelistPackages, "whitelistPackages");
        validateNotNull(whitelistClasses, "whitelistClasses");
        validateNotNull(blacklistPackages, "blacklistPackages");
        validateNotNull(blacklistClasses, "blacklistClasses");
        return new DefaultPackageScanner(whitelistPackages,
                whitelistClasses,
                blacklistPackages,
                blacklistClasses);
    }

    @Override
    public List<Class<?>> scan() {
        try (final ScanResult scanResult = new ClassGraph()
                .whitelistPackages(this.whitelistPackages.toArray(String[]::new))
                .whitelistClasses(this.whitelistClasses
                        .stream()
                        .map(Class::getCanonicalName)
                        .toArray(String[]::new)
                )
                .blacklistPackages(this.blacklistPackages.toArray(String[]::new))
                .blacklistClasses(this.blacklistClasses
                        .stream()
                        .map(Class::getCanonicalName)
                        .toArray(String[]::new)
                )
                .scan()) {
            return scanResult.getAllClasses().loadClasses();
        }
    }
}
