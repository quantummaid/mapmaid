package de.quantummaid.mapmaid.specs.examples.serializedobjects.success.boolean_starting_with_is

class BooleanStartingWithIs(
    val queueUrl: String,
    val isFifoQueue: Boolean,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BooleanStartingWithIs

        if (queueUrl != other.queueUrl) return false
        if (isFifoQueue != other.isFifoQueue) return false

        return true
    }

    override fun hashCode(): Int {
        var result = queueUrl.hashCode()
        result = 31 * result + isFifoQueue.hashCode()
        return result
    }
}