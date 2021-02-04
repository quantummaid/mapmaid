package de.quantummaid.mapmaid.regression

import de.quantummaid.mapmaid.minimaljson.MinimalJsonMarshallerAndUnmarshaller.minimalJsonMarshallerAndUnmarshaller
import de.quantummaid.mapmaid.regression.domain.*
import de.quantummaid.mapmaid.regression.tests.GsonSerializerFactory
import de.quantummaid.mapmaid.regression.tests.MapMaidSerializerFactory
import org.junit.jupiter.api.Test

interface TestSerializerFactory {
    fun create(): TestSerializer

    fun name(): String
}

interface TestSerializer {
    fun serialize(value: Any): String
}

val SERIALIZERS = listOf(
        GsonSerializerFactory(),
        MapMaidSerializerFactory(minimalJsonMarshallerAndUnmarshaller())
)

val stringOf16k = String(ByteArray(16*1000) { 1 })

val OBJECTS = listOf<Any>(
        Impl1(
                MessageId.newUnique(),
                TraceId(stringOf16k),
                ResourceId.newUnique(),
                Endpoint.endpoint("X/X"),
                listOf(
                        SampleObject1(Name("Alfons"), Something("sth"))
                ),
                mapOf(
                        "a" to "A",
                        "b" to "B",
                )
        ),
        Impl2(
                MessageId.newUnique(),
                TraceId(stringOf16k),
                ResourceId.newUnique(),
                Endpoint.endpoint("X/X"),
                SampleObject1(Name("Alfons"), Something("sth")),
                mapOf(
                        Name("B") to listOf(
                                SampleObject3(Something("sth1"), 1)
                        ),
                        Name("C") to listOf(
                                SampleObject3(Something("sth2"), 2),
                                SampleObject3(Something("sth3"), 3)
                        ),
                )
        ),
        Impl4(
                MessageId.newUnique(),
                TraceId(stringOf16k),
                ResourceId.newUnique(),
                Endpoint.endpoint("X/X"),
                MessageId.newUnique(),
                mapOf(
                        Name("B") to listOf(
                                SampleObject2(Name("1"))
                        ),
                        Name("C") to listOf(
                                SampleObject2(Name("2")),
                                SampleObject2(Name("3"))
                        ),
                ),
                listOf(
                        SampleImpl1("A"),
                        SampleImpl2("b"),
                )
        ),
        Impl5(
                MessageId.newUnique(),
                TraceId(stringOf16k),
                ResourceId.newUnique(),
                Endpoint.endpoint("X/X"),
                MessageId.newUnique(),
                listOf(
                        SampleObject3(Something("A"), 1),
                        SampleObject3(Something("B"), 2),
                ),
                Pair("A", Name("A")),
        )
)

class RegressionSpecs {

    @Test
    fun test() {
        SERIALIZERS
                .map { MapperMetrics.record(it) }
                .map { it.render() }
                .forEach { println(it) }
    }
}

class MapperMetrics(val serializerFactory: TestSerializerFactory,
                    val serializerInstantiationTime: Long,
                    val serializationTime: Long) {

    fun render(): String {
        val name = serializerFactory.name()
        return normalize(name) + ":\tinit: " + serializerInstantiationTime + "\tserialize: " + serializationTime
    }

    private fun normalize(string: String): String {
        return string + " ".repeat(22 - string.length)
    }

    companion object {
        fun record(serializerFactory: TestSerializerFactory): MapperMetrics {
            val (serializer, serializerInstantiationTime) = timeOften { serializerFactory.create() }

            val (_, serializationTime) = timeOften {
                OBJECTS.forEach {
                    serializer.serialize(it)
                }
            }

            return MapperMetrics(serializerFactory, serializerInstantiationTime, serializationTime)
        }
    }
}

data class Timed<T>(val result: T, val time: Long)

fun <T> timeOften(code: () -> T): Timed<T> {
    return time {
        repeat(999) {
            code.invoke()
        }
        code.invoke()
    }
}

fun <T> time(code: () -> T): Timed<T> {
    val start = System.currentTimeMillis()
    val result = code.invoke()
    val time = System.currentTimeMillis() - start
    return Timed(result, time)
}
