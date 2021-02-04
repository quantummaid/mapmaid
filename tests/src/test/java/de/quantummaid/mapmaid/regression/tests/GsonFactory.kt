package de.quantummaid.mapmaid.regression.tests

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import de.quantummaid.mapmaid.regression.TestSerializer
import de.quantummaid.mapmaid.regression.TestSerializerFactory
import de.quantummaid.mapmaid.regression.domain.*

class GsonSerializerFactory : TestSerializerFactory {
    override fun create(): TestSerializer {
        val gsonBuilder = GsonBuilder()
        gsonBuilder.setPrettyPrinting()
        gsonBuilder.registerTypeAdapterFactory(TestTypeAdapterFactory())
        val gson = gsonBuilder.create()
        return GsonSerializer(gson)
    }

    override fun name() = "Gson"
}

class GsonSerializer(private val gson: Gson) : TestSerializer {

    override fun serialize(value: Any): String {
        return gson.toJson(value)
    }
}

inline fun <reified T> typeToken(): TypeToken<T> = object : TypeToken<T>() {}

class TestTypeAdapterFactory : TypeAdapterFactory {
    override fun <T : Any> create(gson: Gson, typeToken: TypeToken<T>): TypeAdapter<T> {
        return when (typeToken.rawType) {
            Impl1::class.java -> impl1Adapter(adapter(gson), adapter(gson)) as TypeAdapter<T>
            Impl2::class.java -> impl2Adapter(adapter(gson), adapter(gson)) as TypeAdapter<T>
            Impl4::class.java -> impl4Adapter(adapter(gson), adapter(gson)) as TypeAdapter<T>
            Impl5::class.java -> impl5Adapter(adapter(gson), adapter(gson)) as TypeAdapter<T>
            Name::class.java -> ValueTypeAdapter() as TypeAdapter<T>
            Something::class.java -> ValueTypeAdapter() as TypeAdapter<T>
            else -> gson.getDelegateAdapter(this, typeToken)
        }
    }

    inline fun <reified T> adapter(gson: Gson): TypeAdapter<T> {
        return gson.getDelegateAdapter(this, typeToken())
    }

}

class ValueTypeAdapter : TypeAdapter<ValueType<String>>() {

    override fun write(writer: JsonWriter, valueType: ValueType<String>) {
        writer.value(valueType.mappingValue())
    }

    override fun read(reader: JsonReader): ValueType<String> {
        TODO()
    }
}

fun impl1Adapter(sampleObject1Adapter: TypeAdapter<SampleObject1>,
                 stringMapAdapter: TypeAdapter<Map<String, String>>): TypeAdapter<Impl1> = AdapterBuilder<Impl1>()
        .withPrimitive("messageId") { it.messageId.mappingValue() }
        .withPrimitive("traceId") { it.traceId.mappingValue() }
        .withPrimitive("replyTo") { it.replyTo.mappingValue() }
        .withList("sampleObjectsList", sampleObject1Adapter) { it.sampleObjectsList }
        .withDelegate("stringMap", stringMapAdapter) { it.stringMap }

fun impl2Adapter(sampleObject1Adapter: TypeAdapter<SampleObject1>,
                 sampleObject3Adapter: TypeAdapter<SampleObject3>): TypeAdapter<Impl2> = AdapterBuilder<Impl2>()
        .withPrimitive("messageId") { it.messageId.mappingValue() }
        .withPrimitive("traceId") { it.traceId.mappingValue() }
        .withPrimitive("resourceId") { it.resourceId.mappingValue() }
        .withPrimitive("replyTo") { it.replyTo.mappingValue() }
        .withDelegate("sampleObject", sampleObject1Adapter) { it.sampleObject }
        .withMultiMap("sampleObjectsMap", { it.sampleObjectsMap }, sampleObject3Adapter) { it.mappingValue() }

fun impl4Adapter(sampleObject2Adapter: TypeAdapter<SampleObject2>,
                 sampleInterfaceAdapter: TypeAdapter<SampleInterface>) = AdapterBuilder<Impl4>()
        .withPrimitive("messageId") { it.messageId.mappingValue() }
        .withPrimitive("traceId") { it.traceId.mappingValue() }
        .withPrimitive("resourceId") { it.resourceId.mappingValue() }
        .withPrimitive("destination") { it.destination.mappingValue() }
        .withPrimitive("correlationId") { it.correlationId.mappingValue() }
        .withMultiMap("sampleObjectsMap", { it.sampleObjectsMap }, sampleObject2Adapter) { it.mappingValue() }
        .withList("sampleInterfaces", sampleInterfaceAdapter) { it.sampleInterfaces }

fun impl5Adapter(sampleObject3Adapter: TypeAdapter<SampleObject3>,
                 pairAdapter: TypeAdapter<Pair<String, Name>>) = AdapterBuilder<Impl5>()
        .withPrimitive("messageId") { it.messageId.mappingValue() }
        .withPrimitive("traceId") { it.traceId.mappingValue() }
        .withPrimitive("resourceId") { it.resourceId.mappingValue() }
        .withPrimitive("destination") { it.destination.mappingValue() }
        .withPrimitive("correlationId") { it.correlationId.mappingValue() }
        .withList("sampleInterfaces", sampleObject3Adapter) { it.sampleInterfaces }
        .withDelegate("pair", pairAdapter) { it.pair }

data class AdapterField<T>(val name: String, val query: (T, JsonWriter) -> Unit)

class AdapterBuilder<T> : TypeAdapter<T>() {
    private val fields: MutableList<AdapterField<T>> = ArrayList()

    fun withPrimitive(name: String, query: (T) -> String): AdapterBuilder<T> {
        return withComplex(name) { value, writer ->
            writer.value(query.invoke(value))
        }
    }

    fun <K, V> withMultiMap(name: String, query: (T) -> Map<K, List<V>>, typeAdapter: TypeAdapter<V>, keyMapper: (K) -> String): AdapterBuilder<T> {
        return withComplex(name) { value, writer ->
            writer.beginObject()
            val map = query.invoke(value)
            map.forEach { (key, value) ->
                writer.name(keyMapper.invoke(key))
                writer.beginArray()
                value.forEach { typeAdapter.write(writer, it) }
                writer.endArray()
            }
            writer.endObject()
        }
    }

    fun <V> withList(name: String, typeAdapter: TypeAdapter<V>, query: (T) -> List<V>): AdapterBuilder<T> {
        return withComplex(name) { value, writer ->
            val list = query.invoke(value)
            writer.beginArray()
            list.forEach { typeAdapter.write(writer, it) }
            writer.endArray()
        }
    }

    fun <D> withDelegate(name: String, typeAdapter: TypeAdapter<D>, query: (T) -> D): AdapterBuilder<T> {
        return withComplex(name) { value, writer ->
            val queried = query.invoke(value)
            typeAdapter.write(writer, queried)
        }
    }

    fun withComplex(name: String, query: (T, JsonWriter) -> Unit): AdapterBuilder<T> {
        val adapterField: AdapterField<T> = AdapterField(name, query)
        fields.add(adapterField)
        return this
    }

    override fun write(writer: JsonWriter, value: T) {
        writer.beginObject()
        fields.forEach {
            writer.name(it.name)
            it.query.invoke(value, writer)
        }
        writer.endObject()
    }

    override fun read(reader: JsonReader): T {
        TODO("Not yet implemented")
    }

}
