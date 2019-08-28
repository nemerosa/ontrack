package net.nemerosa.ontrack.extension.scm.relnotes

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
        val itemGroups: List<ReleaseNotesItemGroup>
)

class ReleaseNotesItemGroup(
        val title: String,
        val items: List<ReleaseNotesItem>
)

class ReleaseNotesItem(
        val text: String,
        val annotatedText: List<MessageAnnotation>
)
