package net.nemerosa.ontrack.extension.scm.relnotes

import net.nemerosa.ontrack.extension.scm.relnotes.ReleaseNotes
import net.nemerosa.ontrack.extension.scm.relnotes.ReleaseNotesRequest
import net.nemerosa.ontrack.model.structure.Project

interface ReleaseNotesService {

    fun getProjectReleaseNotes(
            project: Project,
            request: ReleaseNotesRequest
    ): ReleaseNotes

}