package net.nemerosa.ontrack.extension.scm.relnotes

import net.nemerosa.ontrack.model.structure.Build

class ReleaseNotes(
        val groups: List<ReleaseNotesGroup>
)

class ReleaseNotesGroup(
        val title: String,
        val versions: List<ReleaseNotesVersion>
)

class ReleaseNotesVersion(
        val build: Build,
        val notes: String
)
