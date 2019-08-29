package net.nemerosa.ontrack.extension.scm.relnotes

import net.nemerosa.ontrack.common.Document
import net.nemerosa.ontrack.extension.issues.export.IssueExportService
import net.nemerosa.ontrack.extension.issues.export.SectionType
import net.nemerosa.ontrack.model.structure.Build

class ReleaseNotes(
        val groups: List<ReleaseNotesGroup>
)

class ReleaseNotesGroup(
        val title: String?,
        val versions: List<ReleaseNotesVersion>
)

class ReleaseNotesVersion(
        val build: Build,
        val notes: String
)

fun IssueExportService.exportReleaseNotes(releaseNotes: ReleaseNotes): Document {
    return concatSections(
            releaseNotes.groups.map { group ->
                exportReleaseNotesGroup(group)
            }
    )
}

fun IssueExportService.exportReleaseNotesGroup(group: ReleaseNotesGroup): Document {
    return exportSection(
            group.title,
            SectionType.TITLE,
            concatSections(
                    group.versions.map { version -> exportReleaseNotesVersion(version) }
            )
    )
}


fun IssueExportService.exportReleaseNotesVersion(version: ReleaseNotesVersion): Document {
    return exportSection(
            title = version.build.name, // TODO Build label service
            sectionType = SectionType.HEADING,
            content = Document(
                    exportFormat.type,
                    version.notes.toByteArray()
            )
    )
}
