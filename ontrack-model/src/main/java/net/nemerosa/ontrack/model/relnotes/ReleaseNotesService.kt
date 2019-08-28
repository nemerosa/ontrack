package net.nemerosa.ontrack.model.relnotes

import net.nemerosa.ontrack.model.structure.Project

interface ReleaseNotesService {

    fun getProjectReleaseNotes(
            project: Project,
            request: ReleaseNotesRequest
    ): ReleaseNotes

}