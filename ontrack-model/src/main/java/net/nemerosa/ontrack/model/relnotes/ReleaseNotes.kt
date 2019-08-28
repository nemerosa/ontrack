package net.nemerosa.ontrack.model.relnotes

import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.support.MessageAnnotation

class ReleaseNotes(
        val groups: List<ReleaseNotesGroup>
)

class ReleaseNotesGroup(
        val title: String,
        val versions: List<ReleaseNotesVersion>
)

class ReleaseNotesVersion(
        val build: Build,
        val items: List<ReleaseNotesItem>
)

class ReleaseNotesItem(
        val text: String,
        val annotatedText: List<MessageAnnotation>
)
