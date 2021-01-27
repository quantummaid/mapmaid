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

package de.quantummaid.mapmaid.dynamodb.attributevalue;

import de.quantummaid.mapmaid.builder.MarshallerAndUnmarshaller;
import de.quantummaid.mapmaid.mapper.marshalling.Marshaller;
import de.quantummaid.mapmaid.mapper.marshalling.MarshallingType;
import de.quantummaid.mapmaid.mapper.marshalling.Unmarshaller;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import static de.quantummaid.mapmaid.dynamodb.attributevalue.AttributeValueMarshaller.attributeValueMarshaller;
import static de.quantummaid.mapmaid.dynamodb.attributevalue.AttributeValueUnmarshaller.attributeValueUnmarshaller;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class AttributeValueMarshallerAndUnmarshaller implements MarshallerAndUnmarshaller<AttributeValue> {
    public static final MarshallingType<AttributeValue> DYNAMODB_ATTRIBUTEVALUE = MarshallingType.marshallingType("DYNAMODB_ATTRIBUTEVALUE");

    public static AttributeValueMarshallerAndUnmarshaller attributeValueMarshallerAndUnmarshaller() {
        return new AttributeValueMarshallerAndUnmarshaller();
    }

    @Override
    public MarshallingType<AttributeValue> marshallingType() {
        return DYNAMODB_ATTRIBUTEVALUE;
    }

    @Override
    public Marshaller<AttributeValue> marshaller() {
        return attributeValueMarshaller();
    }

    @Override
    public Unmarshaller<AttributeValue> unmarshaller() {
        return attributeValueUnmarshaller();
    }
}
