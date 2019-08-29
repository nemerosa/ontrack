package net.nemerosa.ontrack.extension.scm.relnotes

import net.nemerosa.ontrack.model.exceptions.InputException
import net.nemerosa.ontrack.model.structure.Branch

class ReleaseNotesBranchGroupingMismatchException(
        grouping: String,
        branch: Branch,
        branchPath: String
) : InputException("""Grouping of release notes is based on regex /$grouping/ but
    | ${branch.entityDisplayName} has a SCM path which does not match: $branchPath""".trimMargin())