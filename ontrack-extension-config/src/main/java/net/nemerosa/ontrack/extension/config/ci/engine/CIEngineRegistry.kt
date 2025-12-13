package net.nemerosa.ontrack.extension.config.ci.engine

import org.springframework.stereotype.Component

@Component
class CIEngineRegistry(
    engines: List<CIEngine>,
) {

    private val index = engines.associateBy { it.name }

    fun findCIEngine(name: String): CIEngine? = index[name]

    val engines = index.values.toList()

}