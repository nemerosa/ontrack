package net.nemerosa.ontrack.extension.config.scm

import org.springframework.stereotype.Component

@Component
class SCMEngineRegistry(
    scmEngines: List<SCMEngine>,
) {
    private val index = scmEngines.associateBy { it.name }

    fun findSCMEngine(name: String): SCMEngine? = index[name]
}