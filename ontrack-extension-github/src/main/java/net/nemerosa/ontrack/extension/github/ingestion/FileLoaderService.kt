package net.nemerosa.ontrack.extension.github.ingestion

import net.nemerosa.ontrack.extension.github.model.GitHubEngineConfiguration
import net.nemerosa.ontrack.model.structure.Branch

interface FileLoaderService {

    fun loadFile(configuration: GitHubEngineConfiguration, repository: String, branch: String, path: String): String?

    fun loadFile(branch: Branch, path: String): String?

}