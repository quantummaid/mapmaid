package de.quantummaid.mapmaid.regression.domain

import java.util.*

interface ValueType<T> {
    fun mappingValue(): T
}

class ValidatedValueType<T>(private val input: T) : ValueType<T> {
    override fun mappingValue() = input
}

class MessageGroup(private val unsafeInput: String) : ValueType<String> by ValidatedValueType(unsafeInput)

//

class MessageId constructor(private val unsafeInput: String) : ValueType<String> by ValidatedValueType(unsafeInput) {

    companion object {
        fun newUnique(): MessageId {
            return MessageId(UUID.randomUUID().toString())
        }
    }
}

class TraceId(private val value: String) : ValueType<String> by ValidatedValueType(value) {

    fun extend(subTraceId: String): TraceId {
        return extend(TraceId(subTraceId))
    }

    fun extend(subTraceId: TraceId): TraceId {
        return TraceId("$value/${subTraceId.value}")
    }
}

class ResourceId constructor(private val unsafeInput: String) : ValueType<String> by ValidatedValueType(unsafeInput) {
    companion object {
        fun newUnique(): ResourceId {
            return ResourceId(UUID.randomUUID().toString())
        }
    }
}


class Endpoint(private val unsafeInput: String) : ValueType<String> by ValidatedValueType(unsafeInput) {

    companion object {

        fun endpoint(value: String): Endpoint {
            return Endpoint(value)
        }

        fun endpoint(messageGroup: MessageGroup): Endpoint {
            return Endpoint(messageGroup.mappingValue())
        }
    }
}

class Name constructor(private val value: String) : ValueType<String> by ValidatedValueType(value)
class Something constructor(private val value: String) : ValueType<String> by ValidatedValueType(value)


data class SampleObject1(val name: Name, val something: Something)

data class SampleObject2(val name: Name)

data class SampleObject3(val something: Something, val long: Long)

interface SampleInterface

data class SampleImpl1(val value: String) : SampleInterface

data class SampleImpl2(val value: String) : SampleInterface
