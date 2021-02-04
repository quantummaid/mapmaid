package de.quantummaid.mapmaid.regression.domain

interface MyInterface {
    val messageId: MessageId
    val traceId: TraceId
    val resourceId: ResourceId
}

sealed class MySealedClass(
        override val messageId: MessageId,
        override val traceId: TraceId,
        override val resourceId: ResourceId,
        open val replyTo: Endpoint
) : MyInterface

data class Impl1(
        override val messageId: MessageId,
        override val traceId: TraceId,
        override val resourceId: ResourceId,
        override val replyTo: Endpoint,
        val sampleObjectsList: List<SampleObject1>,
        val stringMap: Map<String, String>,
) : MySealedClass(messageId, traceId, resourceId, replyTo)

data class Impl2(
        override val messageId: MessageId,
        override val traceId: TraceId,
        override val resourceId: ResourceId,
        override val replyTo: Endpoint,
        val sampleObject: SampleObject1,
        val sampleObjectsMap: Map<Name, List<SampleObject3>>,
) : MySealedClass(messageId, traceId, resourceId, replyTo)

sealed class Impl3(
        override val messageId: MessageId,
        override val traceId: TraceId,
        override val resourceId: ResourceId,
        open val destination: Endpoint,
        open val correlationId: MessageId
) : MyInterface

data class Impl4(
        override val messageId: MessageId,
        override val traceId: TraceId,
        override val resourceId: ResourceId,
        override val destination: Endpoint,
        override val correlationId: MessageId,
        val sampleObjectsMap: Map<Name, List<SampleObject2>>,
        val sampleInterfaces: List<SampleInterface>,
) : Impl3(messageId, traceId, resourceId, destination, correlationId)

data class Impl5(
        override val messageId: MessageId,
        override val traceId: TraceId,
        override val resourceId: ResourceId,
        override val destination: Endpoint,
        override val correlationId: MessageId,
        val sampleInterfaces: List<SampleObject3>,
        val pair: Pair<String, Name>
) : Impl3(messageId, traceId, resourceId, destination, correlationId)


sealed class Impl6(
        override val messageId: MessageId,
        override val traceId: TraceId,
        override val resourceId: ResourceId
) : MyInterface
