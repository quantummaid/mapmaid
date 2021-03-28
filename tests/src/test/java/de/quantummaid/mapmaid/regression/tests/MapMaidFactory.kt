package de.quantummaid.mapmaid.regression.tests

import com.google.gson.Gson
import de.quantummaid.mapmaid.MapMaid
import de.quantummaid.mapmaid.builder.MarshallerAndUnmarshaller
import de.quantummaid.mapmaid.mapper.marshalling.Marshaller
import de.quantummaid.mapmaid.mapper.marshalling.MarshallingType
import de.quantummaid.mapmaid.mapper.marshalling.Unmarshaller
import de.quantummaid.mapmaid.regression.REFLECT_MAID
import de.quantummaid.mapmaid.regression.TestSerializer
import de.quantummaid.mapmaid.regression.TestSerializerFactory
import de.quantummaid.mapmaid.regression.domain.*
import de.quantummaid.mapmaid.regression.mapmaidextensions.ClassSeAndDeserializer
import de.quantummaid.mapmaid.regression.mapmaidextensions.PairSeAndDeserializer.Companion.pairSeAndDeserializer
import de.quantummaid.mapmaid.regression.mapmaidextensions.StaticallyTypedListSeAndDeserializer
import de.quantummaid.mapmaid.regression.mapmaidextensions.StaticallyTypedMapSeAndDeserializer
import de.quantummaid.reflectmaid.GenericType.Companion.genericType
import de.quantummaid.reflectmaid.ReflectMaid

class MapMaidSerializerFactory(val reflectMaid: ReflectMaid,
                               val marshallerAndUnmarshaller: MarshallerAndUnmarshaller<String>) : TestSerializerFactory {
    override fun create(): TestSerializer {
        val classes = listOf(
                MyInterface::class,
                MySealedClass::class,
                Impl3::class,
                Impl6::class,
        ).flatMap { it.sealedSubclasses }
                .map { it.java }
        val mapMaid = MapMaid.aMapMaid(REFLECT_MAID)
                .serializingAndDeserializingSubtypes(
                        MyInterface::class.java,
                        *classes.toTypedArray()
                )
                .serializingAndDeserializing(ClassSeAndDeserializer(reflectMaid))
                .serializingAndDeserializingCustomPrimitive(
                        Endpoint::class.java,
                        { it.mappingValue() },
                        { Endpoint.endpoint(it) } //
                )
                .serializingAndDeserializing(
                        StaticallyTypedListSeAndDeserializer.staticallyTypedListSeAndDeserializer(
                                SampleObject1::class.java,
                                reflectMaid
                        )
                )
                .serializingAndDeserializing(
                        StaticallyTypedMapSeAndDeserializer.staticallyTypedMapSeAndDeserializer(
                                genericType(Name::class.java),
                                genericType<List<SampleObject2>>(),
                                reflectMaid
                        )
                )
                .serializingAndDeserializing(
                        StaticallyTypedMapSeAndDeserializer.staticallyTypedMapSeAndDeserializer(
                                genericType(Name::class.java),
                                genericType<List<SampleObject3>>(),
                                reflectMaid
                        )
                )
                .serializingAndDeserializing(
                        StaticallyTypedMapSeAndDeserializer.staticallyTypedMapSeAndDeserializer(
                                String::class.java,
                                String::class.java,
                                reflectMaid
                        )
                )
                .serializingAndDeserializingSubtypes(
                        SampleInterface::class.java,
                        SampleImpl1::class.java,
                        SampleImpl2::class.java,
                )
                .serializingAndDeserializing(genericType<List<SampleObject3>>())
                .serializingAndDeserializing(
                        pairSeAndDeserializer(
                                genericType(String::class),
                                genericType(Name::class),
                                reflectMaid
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

class GsonMarshallerAndUnmarshaller() : MarshallerAndUnmarshaller<String> {
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
