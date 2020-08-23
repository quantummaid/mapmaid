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

package de.quantummaid.mapmaid.customtypes;

import de.quantummaid.mapmaid.customcollection.InlinedCollectionFactory;
import de.quantummaid.mapmaid.customcollection.InlinedCollectionListExtractor;
import de.quantummaid.mapmaid.customtypes.customprimitive.CustomCustomPrimitiveDeserializer;
import de.quantummaid.mapmaid.customtypes.customprimitive.CustomCustomPrimitiveSerializer;
import de.quantummaid.mapmaid.customtypes.serializedobject.Builder;
import de.quantummaid.mapmaid.identifier.TypeIdentifier;
import de.quantummaid.reflectmaid.GenericType;

public interface CustomTypeDriver<T> {

    T serializationOnlyCollection(TypeIdentifier collectionType,
                                  TypeIdentifier contentType,
                                  InlinedCollectionListExtractor<?, ?> listExtractor);

    T deserializationOnlyCollection(TypeIdentifier collectionType,
                                    TypeIdentifier contentType,
                                    InlinedCollectionFactory<?, ?> collectionFactory);

    T duplexCollection(TypeIdentifier collectionType,
                       TypeIdentifier contentType,
                       InlinedCollectionListExtractor<?, ?> listExtractor,
                       InlinedCollectionFactory<?, ?> collectionFactory);

    <A, B> T serializationOnlyPrimitive(GenericType<A> type,
                                        CustomCustomPrimitiveSerializer<A, B> serializer,
                                        Class<B> baseType);

    <A, B> T deserializationOnlyPrimitive(GenericType<A> type,
                                          CustomCustomPrimitiveDeserializer<A, B> deserializer,
                                          Class<B> baseType);

    <A, B> T duplexPrimitive(TypeIdentifier type,
                             CustomCustomPrimitiveSerializer<A, B> serializer,
                             CustomCustomPrimitiveDeserializer<A, B> deserializer,
                             Class<B> baseType);

    T serializationOnlyObject(Builder builder);

    T deserializationOnlyObject(Builder builder);

    T duplexObject(Builder builder);
}
