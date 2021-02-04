package de.quantummaid.mapmaid.regression.tests

import com.google.gson.Gson
import de.quantummaid.mapmaid.MapMaid
import de.quantummaid.mapmaid.builder.MarshallerAndUnmarshaller
import de.quantummaid.mapmaid.builder.customtypes.DuplexType
import de.quantummaid.mapmaid.mapper.marshalling.Marshaller
import de.quantummaid.mapmaid.mapper.marshalling.MarshallingType
import de.quantummaid.mapmaid.mapper.marshalling.Unmarshaller
import de.quantummaid.mapmaid.regression.TestSerializer
import de.quantummaid.mapmaid.regression.TestSerializerFactory
import de.quantummaid.mapmaid.regression.domain.*
import de.quantummaid.mapmaid.regression.mapmaidextensions.*
import de.quantummaid.mapmaid.shared.identifier.TypeIdentifier

class MapMaidSerializerFactory(val marshallerAndUnmarshaller: MarshallerAndUnmarshaller<String>) : TestSerializerFactory {
    override fun create(): TestSerializer {
        val classes = listOf(
                MyInterface::class,
                MySealedClass::class,
                Impl3::class,
                Impl6::class,
        ).flatMap { it.sealedSubclasses }
                .map { it.java }
        val mapMaid = MapMaid.aMapMaid()
                .serializingAndDeserializingSubtypes(
                        MyInterface::class.java,
                        *classes.toTypedArray()
                )
                .serializingAndDeserializing(ClassSeAndDeserializer.classSeAndDeserializer())
                .serializingAndDeserializing(
                        DuplexType.customPrimitive(
                                Endpoint::class.java,
                                { it.mappingValue() },
                                { Endpoint.endpoint(it) } //
                        )
                )
                .serializingAndDeserializing(
                        StaticallyTypedListSeAndDeserializer.staticallyTypedListSeAndDeserializer(
                                SampleObject1::class.java,
                        )
                )
                .serializingAndDeserializing(
                        StaticallyTypedMapSeAndDeserializer.staticallyTypedMapSeAndDeserializer(
                                TypeIdentifier.typeIdentifierFor(Name::class.java),
                                TypeIdentifier.typeIdentifierFor(genericType<List<SampleObject2>>())
                        )
                )
                .serializingAndDeserializing(
                        StaticallyTypedMapSeAndDeserializer.staticallyTypedMapSeAndDeserializer(
                                TypeIdentifier.typeIdentifierFor(Name::class.java),
                                TypeIdentifier.typeIdentifierFor(genericType<List<SampleObject3>>())
                        )
                )
                .serializingAndDeserializing(
                        StaticallyTypedMapSeAndDeserializer.staticallyTypedMapSeAndDeserializer(
                                String::class.java,
                                String::class.java,
                        )
                )
                .serializingAndDeserializingSubtypes(
                        SampleInterface::class.java,
                        SampleImpl1::class.java,
                        SampleImpl2::class.java,
                )
                .serializingAndDeserializing(genericType<List<SampleObject3>>())
                .serializingAndDeserializing(
                        PairSeAndDeserializer(
                                TypeIdentifier.typeIdentifierFor(String::class.java),
                                TypeIdentifier.typeIdentifierFor(Name::class.java)
                        )
                )
                .withAdvancedSettings { it.usingMarshaller(marshallerAndUnmarshaller) }
                .build()
        return MapMaidSerializer(mapMaid, marshallerAndUnmarshaller.marshallingType())
    }

    override fun name() = "MapMaid (${marshallerAndUnmarshaller.marshallingType().internalValueForMapping()})"
}

class MapMaidSerializer(private val mapMaid: MapMaid, private val marshallingType: MarshallingType<String>) : TestSerializer {

    override fun serialize(value: Any): String {
        val string = mapMaid.serializeTo(value, marshallingType)
        return string
    }
}

class GsonMarshallerAndUnmarshaller(): MarshallerAndUnmarshaller<String> {
    private val gson = Gson()

    override fun marshallingType(): MarshallingType<String> {
        return MarshallingType.marshallingType("gson")
    }

    override fun marshaller(): Marshaller<String> {
        return Marshaller<String> { gson.toJson(it) }
    }

    override fun unmarshaller(): Unmarshaller<String> {
        return Unmarshaller<String> { gson.fromJson(it, Any::class.java) }
    }
}
