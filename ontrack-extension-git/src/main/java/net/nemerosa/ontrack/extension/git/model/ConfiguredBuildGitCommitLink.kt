package net.nemerosa.ontrack.extension.git.model

import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.ServiceConfiguration

/**
 * Configured [net.nemerosa.ontrack.extension.git.model.BuildGitCommitLink].
 */
class ConfiguredBuildGitCommitLink<T>(
        val link: BuildGitCommitLink<T>,
        val data: T
) {

    fun clone(replacementFunction: (String) -> String): ConfiguredBuildGitCommitLink<T> {
        return ConfiguredBuildGitCommitLink(
                link,
                link.clone(data, replacementFunction)
        )
    }

    fun getCommitFromBuild(build: Build): String {
        return link.getCommitFromBuild(build, data)
    }

    fun isBuildNameValid(name: String): Boolean {
        return link.isBuildNameValid(name, data)
    }

    fun toServiceConfiguration(): ServiceConfiguration {
        return ServiceConfiguration(
                link.id,
                link.toJson(data)
        )
    }
}
