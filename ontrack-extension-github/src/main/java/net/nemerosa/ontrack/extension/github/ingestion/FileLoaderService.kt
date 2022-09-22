package net.nemerosa.ontrack.extension.github.ingestion

import net.nemerosa.ontrack.model.structure.Branch

interface FileLoaderService {

    fun loadFile(branch: Branch, path: String): String?

}