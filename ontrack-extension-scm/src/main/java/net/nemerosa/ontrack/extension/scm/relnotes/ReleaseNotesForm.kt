package net.nemerosa.ontrack.extension.scm.relnotes

import net.nemerosa.ontrack.model.exceptions.InputException

class ReleaseNotesForm {

    var branchPattern = "release/.*"

    var branchGrouping = ""

    var branchGroupFormat = "Release %s"

    var buildLimit = 10

    var branchOrdering: String? = null

    var promotion: String? = null

    var format = "text"

    var issueGrouping: String = ""
    var issueExclude: String = ""
    var issueAltGroup: String = "Other"

}

class ReleaseNotesBranchOrderingMissingException : InputException("Branch ordering is required.")

class ReleaseNotesPromotionMissingException : InputException("Promotion is required.")
