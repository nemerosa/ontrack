package net.nemerosa.ontrack.model.templating

import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parse

/**
 * High-level configuration for a templating source.
 */
data class TemplatingSourceConfig(
    val params: Map<String, List<String>> = emptyMap(),
) {
    fun isEmpty(): Boolean = params.isEmpty()
    fun getString(name: String): String? = params[name]?.firstOrNull()
    fun getList(name: String): List<String> = params[name] ?: emptyList()
    fun getBoolean(name: String, defaultValue: Boolean = false): Boolean = getString(name)?.toBoolean() ?: defaultValue
    fun getInt(name: String): Int? = getString(name)?.toInt()
    fun getLong(name: String): Long? = getString(name)?.toLong()

    inline fun <reified T: Any> parse(): T {
        val map = params.mapValues { (_, values) ->
            if (values.size == 1) {
                values.first()
            } else {
                values
            }
        }
        return map.asJson().parse()
    }

    companion object {
        fun fromMap(vararg params: Pair<String, String>) = TemplatingSourceConfig(
            params = params.associate { it.first to listOf(it.second) }
        )
    }
}