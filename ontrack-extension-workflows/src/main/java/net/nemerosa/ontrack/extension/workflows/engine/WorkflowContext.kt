package net.nemerosa.ontrack.extension.workflows.engine

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.json.parse

data class WorkflowContext(
    val data: List<WorkflowContextData>,
) {

    constructor(key: String, value: JsonNode) : this(
        listOf(
            WorkflowContextData(key, value)
        )
    )

    private val index = data.associate { it.key to it.value }

    inline fun <reified T> parse(key: String): T {
        val value = getValue(key)
        return value.parse()
    }

    fun getValue(key: String): JsonNode {
        return findValue(key) ?: throw WorkflowContextKeyNotFoundException(key)
    }

    fun findValue(key: String): JsonNode? = index[key]

    fun withData(key: String, value: JsonNode) = WorkflowContext(
        data = data + WorkflowContextData(key, value)
    )

    companion object {
        fun noContext() = WorkflowContext(emptyList())
    }

}
