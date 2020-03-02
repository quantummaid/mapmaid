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

package de.quantummaid.mapmaid.builder.conventional;

import de.quantummaid.mapmaid.builder.conventional.annotations.*;
import de.quantummaid.mapmaid.builder.detection.DefinitionFactory;
import de.quantummaid.mapmaid.builder.detection.customprimitive.deserialization.CustomPrimitiveDeserializationDetector;
import de.quantummaid.mapmaid.builder.detection.customprimitive.serialization.CustomPrimitiveSerializationDetector;
import de.quantummaid.mapmaid.builder.detection.serializedobject.deserialization.ConstructorBasedDeserializationDetector;
import de.quantummaid.mapmaid.builder.detection.serializedobject.deserialization.SerializedObjectDeserializationDetector;
import de.quantummaid.mapmaid.builder.detection.serializedobject.fields.FieldDetector;
import de.quantummaid.mapmaid.mapper.universal.UniversalBoolean;
import de.quantummaid.mapmaid.mapper.universal.UniversalNumber;
import de.quantummaid.mapmaid.mapper.universal.UniversalString;
import de.quantummaid.mapmaid.shared.mapping.CustomPrimitiveMappings;
import de.quantummaid.mapmaid.builder.detection.customprimitive.CustomPrimitiveDefinitionFactory;

import java.util.LinkedList;
import java.util.List;

import static de.quantummaid.mapmaid.builder.detection.customprimitive.deserialization.ClassAnnotationBasedCustomPrimitiveDeserializationDetector.classAnnotationBasedDeserializer;
import static de.quantummaid.mapmaid.builder.detection.customprimitive.deserialization.ConstructorBasedCustomPrimitiveDeserializationDetector.constructorBased;
import static de.quantummaid.mapmaid.builder.detection.customprimitive.deserialization.MethodAnnotationBasedCustomPrimitiveDeserializationDetector.annotationBasedDeserializer;
import static de.quantummaid.mapmaid.builder.detection.customprimitive.deserialization.StaticMethodBasedCustomPrimitiveDeserializationDetector.staticMethodBased;
import static de.quantummaid.mapmaid.builder.detection.customprimitive.serialization.ClassAnnotationBasedCustomPrimitiveSerializationDetector.classAnnotationBasedSerializer;
import static de.quantummaid.mapmaid.builder.detection.customprimitive.serialization.MethodAnnotationBasedCustomPrimitiveSerializationDetector.annotationBasedSerializer;
import static de.quantummaid.mapmaid.builder.detection.customprimitive.serialization.MethodNameBasedCustomPrimitiveSerializationDetector.methodNameBased;
import static de.quantummaid.mapmaid.builder.detection.serializedobject.ClassFilter.allowAll;
import static de.quantummaid.mapmaid.builder.detection.serializedobject.SerializedObjectDefinitionFactory.serializedObjectFactory;
import static de.quantummaid.mapmaid.builder.detection.serializedobject.deserialization.AnnotationBasedDeserializationDetector.annotationBasedDeserialzer;
import static de.quantummaid.mapmaid.builder.detection.serializedobject.deserialization.MatchingMethodDeserializationDetector.matchingMethodBased;
import static de.quantummaid.mapmaid.builder.detection.serializedobject.deserialization.NamedMethodDeserializationDetector.namedMethodBased;
import static de.quantummaid.mapmaid.builder.detection.serializedobject.deserialization.SetterBasedDeserializationDetector.setterBasedDeserializationDetector;
import static de.quantummaid.mapmaid.builder.detection.serializedobject.deserialization.SingleMethodDeserializationDetector.singleMethodBased;
import static de.quantummaid.mapmaid.builder.detection.serializedobject.fields.AnnotationFieldDetector.annotationBased;
import static de.quantummaid.mapmaid.builder.detection.serializedobject.fields.GetterFieldDetector.getterFieldDetector;
import static de.quantummaid.mapmaid.builder.detection.serializedobject.fields.ModifierFieldDetector.modifierBased;
import static de.quantummaid.mapmaid.mapper.universal.UniversalNumber.universalNumber;
import static de.quantummaid.mapmaid.shared.mapping.CustomPrimitiveMappings.customPrimitiveMappings;
import static de.quantummaid.mapmaid.shared.mapping.UniversalTypeMapper.universalTypeMapper;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

public final class ConventionalDefinitionFactories {

    public static final CustomPrimitiveMappings CUSTOM_PRIMITIVE_MAPPINGS = customPrimitiveMappings(
            universalTypeMapper(String.class, UniversalString.class),
            universalTypeMapper(double.class, UniversalNumber.class),
            universalTypeMapper(Double.class, UniversalNumber.class),
            universalTypeMapper(boolean.class, UniversalBoolean.class),
            universalTypeMapper(Boolean.class, UniversalBoolean.class),
            universalTypeMapper(int.class, UniversalNumber.class,
                    integer -> universalNumber(Double.valueOf(integer)),
                    universalNumber -> ((Double) universalNumber.toNativeJava()).intValue()),
            universalTypeMapper(Integer.class, UniversalNumber.class,
                    integer -> universalNumber(Double.valueOf(integer)),
                    universalNumber -> ((Double) universalNumber.toNativeJava()).intValue())
    );

    private ConventionalDefinitionFactories() {
    }

    public static DefinitionFactory nameAndConstructorBasedCustomPrimitiveDefinitionFactory(
            final String serializationMethodName,
            final String deserializationMethodName) {
        return CustomPrimitiveDefinitionFactory.customPrimitiveFactory(
                methodNameBased(CUSTOM_PRIMITIVE_MAPPINGS, serializationMethodName),
                staticMethodBased(CUSTOM_PRIMITIVE_MAPPINGS, deserializationMethodName),
                constructorBased(CUSTOM_PRIMITIVE_MAPPINGS)
        );
    }

    public static DefinitionFactory customPrimitiveMethodAnnotationFactory() {
        final CustomPrimitiveSerializationDetector serializationDetector = annotationBasedSerializer(MapMaidPrimitiveSerializer.class);
        final CustomPrimitiveDeserializationDetector deserializationDetector = annotationBasedDeserializer(MapMaidPrimitiveDeserializer.class);
        return CustomPrimitiveDefinitionFactory.customPrimitiveFactory(serializationDetector, deserializationDetector);
    }

    public static DefinitionFactory customPrimitiveClassAnnotationFactory() {
        final CustomPrimitiveSerializationDetector serializationDetector =
                classAnnotationBasedSerializer(MapMaidPrimitive.class, MapMaidPrimitive::serializationMethodName);
        final CustomPrimitiveDeserializationDetector deserializationDetector =
                classAnnotationBasedDeserializer(MapMaidPrimitive.class, MapMaidPrimitive::deserializationMethodName);
        return CustomPrimitiveDefinitionFactory.customPrimitiveFactory(serializationDetector, deserializationDetector);
    }

    public static DefinitionFactory pojoSerializedObjectFactory() {
        return serializedObjectFactory(allowAll(), singletonList(getterFieldDetector()), singletonList(setterBasedDeserializationDetector()));
    }

    public static DefinitionFactory allSerializedObjectFactory(final String deserializationMethodName) {
        final List<FieldDetector> fieldDetectors = new LinkedList<>();
        fieldDetectors.add(annotationBased(MapMaidSerializedField.class));
        fieldDetectors.add(modifierBased());

        final SerializedObjectDeserializationDetector namedMethodBased = namedMethodBased(deserializationMethodName);
        final SerializedObjectDeserializationDetector singleMethod = singleMethodBased();
        final SerializedObjectDeserializationDetector matchingMethod = matchingMethodBased(deserializationMethodName);
        final SerializedObjectDeserializationDetector constructor = ConstructorBasedDeserializationDetector.constructorBased();
        final List<SerializedObjectDeserializationDetector> deserializationDetectors =
                asList(namedMethodBased, singleMethod, matchingMethod, constructor);

        return serializedObjectFactory(allowAll(), fieldDetectors, deserializationDetectors);
    }

    public static DefinitionFactory serializedObjectClassAnnotationFactory() {
        final FieldDetector fieldDetector = annotationBased(MapMaidSerializedField.class);
        final SerializedObjectDeserializationDetector deserializationDetector = annotationBasedDeserialzer(MapMaidDeserializationMethod.class);
        return serializedObjectFactory(allowAll(), asList(fieldDetector), singletonList(deserializationDetector));
    }
}
