package net.nemerosa.ontrack.extension.scm.relnotes

import net.nemerosa.ontrack.model.exceptions.InputException

class ReleaseNotesBranchGroupingFormatException(
        grouping: String
) : InputException("""Grouping of release notes is based on regex /$grouping/ but
    | it must contains at least one capturing group.""".trimMargin())