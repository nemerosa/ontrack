package net.nemerosa.ontrack.extension.scm.relnotes

// TODO

class ReleaseNotesRequest(
        val branchPattern: String,
        val branchGrouping: String,
        val branchOrdering: String,
        val buildLimit: Int,
        var promotion: String,
        val issueGrouping: String,
        val issueExclude: String,
        val issueAltGroup: String
)
