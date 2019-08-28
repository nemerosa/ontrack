package net.nemerosa.ontrack.extension.scm.relnotes

import net.nemerosa.ontrack.model.exceptions.InputException

class ReleaseNotesForm {

    var branchPattern = "release/.*"

    var branchGrouping = ""

    var buildLimit = 10

    var branchOrdering: String? = null

    var promotion: String? = null

}

class ReleaseNotesBranchOrderingMissingException : InputException("Branch ordering is required.")

class ReleaseNotesPromotionMissingException : InputException("Promotion is required.")
