package net.nemerosa.ontrack.extension.config.model

import net.nemerosa.ontrack.extension.config.ci.engine.CIEngine
import net.nemerosa.ontrack.extension.config.scm.SCMEngine
import net.nemerosa.ontrack.model.structure.Project

interface CoreConfigurationService {
    fun configureProject(
        configuration: ConfigurationInput,
        ciEngine: CIEngine,
        scmEngine: SCMEngine,
        env: Map<String, String>
    ): Project
}