package net.nemerosa.ontrack.extension.scm.relnotes

/**
 * @property issueGrouping See [net.nemerosa.ontrack.extension.api.model.IssueChangeLogExportRequest.grouping]
 */
class ReleaseNotesRequest(
        val branchPattern: String,
        val branchGrouping: String,
        val branchGroupFormat: String,
        val branchOrdering: String,
        val branchOrderingParameter: String?,
        val buildLimit: Int,
        var promotion: String,
        val format: String,
        val issueGrouping: String,
        val issueExclude: String,
        val issueAltGroup: String
)
