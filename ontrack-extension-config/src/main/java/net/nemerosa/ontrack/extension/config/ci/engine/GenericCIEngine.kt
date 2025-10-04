package net.nemerosa.ontrack.extension.config.ci.engine

import org.springframework.stereotype.Component

@Component
class GenericCIEngine : CIEngine {
    override val name: String = "generic"

    override fun getProjectName(env: Map<String, String>): String? = env["PROJECT_NAME"]

    override fun getBuildSuffix(env: Map<String, String>): String? = env["BUILD_NUMBER"]
}