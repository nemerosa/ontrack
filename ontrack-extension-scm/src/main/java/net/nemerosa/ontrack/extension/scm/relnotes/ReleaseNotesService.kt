package net.nemerosa.ontrack.extension.scm.relnotes

import net.nemerosa.ontrack.common.Document
import net.nemerosa.ontrack.model.structure.Project

interface ReleaseNotesService {

    fun getProjectReleaseNotes(project: Project, request: ReleaseNotesRequest): ReleaseNotes

    fun exportProjectReleaseNotes(project: Project, request: ReleaseNotesRequest): Document

}