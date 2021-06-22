package de.quantummaid.mapmaid.specs.polymorphy

import de.quantummaid.mapmaid.MapMaid
import org.junit.jupiter.api.Test
import java.util.*

data class IncrementRequest(val id: RequestId, val field1: String)

data class RequestId private constructor(val value: String) {
    companion object {
        fun newRequestId(): RequestId = RequestId(UUID.randomUUID().toString())

        @JvmStatic
        fun requestId(unsafe: String) = RequestId(UUID.fromString(unsafe).toString())
    }
}

sealed class IncrementResponse {
    companion object {

        fun success(
            request: IncrementRequest,
            result: Int
        ): IncrementResponse = IncrementSuccessResponse(request.id, result)

        fun failure(
            request: IncrementRequest,
            reason: String
        ): IncrementResponse = IncrementFailureResponse(request.id, reason)
    }

    internal abstract val correlationId: RequestId
}

data class IncrementFailureResponse(
    override val correlationId: RequestId,
    val reason: String
) : IncrementResponse()

data class IncrementSuccessResponse(
    override val correlationId: RequestId,
    val result: Int
) : IncrementResponse()

sealed class OutboundMessage
data class SendIncrementNotification(val event: String) : OutboundMessage()
data class SendIncrementResponse(val response: String) : OutboundMessage()


fun runCommand(command: String): String {
    val process = Runtime.getRuntime().exec(command)
    process.waitFor()
    val stdout = process.inputStream.bufferedReader().use { it.readText() }
    val stderr = process.errorStream.bufferedReader().use { it.readText() }
    val exitValue = process.exitValue()
    if (exitValue != 0) {
        throw IllegalStateException("command $command failed with exit value $exitValue, stdout: $stdout, stderr: $stderr")
    }
    return stdout
}

class SealedClassesSpecs {


    @Test
    fun mapMaidCanSerializeKotlinSealedClasses() {
        runCommand("")

        val mapMaid = MapMaid.aMapMaid()
            .serializingAndDeserializing(OutboundMessage::class.java)
            .build()

        val dumpAll = mapMaid.debugInformation().dumpAll()
        println(dumpAll)
    }
}