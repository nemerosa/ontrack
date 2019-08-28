package net.nemerosa.ontrack.extension.scm.relnotes

import net.nemerosa.ontrack.extension.scm.model.SCMChangeLogIssue
import net.nemerosa.ontrack.extension.scm.model.SCMChangeLogIssues
import net.nemerosa.ontrack.model.extension.Extension
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.Project

/**
 * This extension point allows an extension to participate into the generation of release notes.
 */
interface ReleaseNotesGenerationExtension : Extension {

    /**
     * Checks if this extension applies to the given project
     */
    fun appliesForProject(project: Project): Boolean

    /**
     * Change log between two builds
     */
    fun <T : SCMChangeLogIssue> changeLog(from: Build, to: Build): SCMChangeLogIssues<T>?

}