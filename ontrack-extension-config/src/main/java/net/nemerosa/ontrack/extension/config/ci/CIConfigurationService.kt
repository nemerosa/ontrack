package net.nemerosa.ontrack.extension.config.ci

import net.nemerosa.ontrack.extension.config.model.CIEnv
import net.nemerosa.ontrack.model.structure.Build

/**
 * Configuration of builds, branches and projects based on some configuration and context.
 */
interface CIConfigurationService {

    /**
     * Given a CI configuration and a context, returns a build.
     *
     * @param config YAML configuration
     * @param ci Name of the CI provider (like "jenkins")
     * @param scm Name of the SCM provider (like "github")
     * @param env Environment variables
     * @return Configured build
     */
    fun configureBuild(
        config: String,
        ci: String?,
        scm: String?,
        env: List<CIEnv>,
    ): Build

}