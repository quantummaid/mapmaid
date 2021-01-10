package de.quantummaid.mapmaid.specs.reflectionelimination

import de.quantummaid.mapmaid.MapMaid
import de.quantummaid.mapmaid.builder.customtypes.DeserializationOnlyType
import de.quantummaid.mapmaid.builder.customtypes.DuplexType
import de.quantummaid.mapmaid.builder.customtypes.SerializationOnlyType
import de.quantummaid.reflectmaid.GenericType

data class Foo(val field: String)
data class Bar(val field1: String, val field2: Foo, val field3: Int)

fun main() {
    val mapMaid = MapMaid.aMapMaid()
            .serializingAndDeserializing(Bar::class.java)
            .build()

    val s = mapMaid.eliminateReflections();
    println(s)

    MapMaid.aMapMaid()
            .serializingAndDeserializing(DuplexType.stringBasedCustomPrimitive(GenericType.genericType(Foo::class.java), { it.field }, { Foo(it) }))
            .serializingAndDeserializing(DuplexType.serializedObject(GenericType.genericType(Bar::class.java))
                    .withField("field1", GenericType.genericType(String::class.java), {  it.field1  })
                    .withField("field2", GenericType.genericType(Foo::class.java), {  it.field2  })
                    .withField("field3", GenericType.genericType(Int::class.java), {  it.field3  })
                    .deserializedUsing {  parameter0, parameter1, parameter2 -> Bar(parameter0, parameter1, parameter2)  })
            .build()
}
