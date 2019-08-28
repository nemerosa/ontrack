package net.nemerosa.ontrack.extension.git.relnotes

import net.nemerosa.ontrack.extension.api.model.IssueChangeLogExportRequest
import net.nemerosa.ontrack.extension.issues.export.ExportedIssues

interface GitReleaseNotesGenerationService {
    fun changeLog(changeLogRequest: IssueChangeLogExportRequest): ExportedIssues?
}