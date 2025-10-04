package net.nemerosa.ontrack.extension.config.scm

import net.nemerosa.ontrack.model.structure.NameDescription

interface SCMEngine {
    fun normalizeBranchName(rawBranchName: String): String = NameDescription.escapeName(rawBranchName)

    val name: String
}