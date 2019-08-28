package net.nemerosa.ontrack.model.relnotes

class ReleaseNotesRequest(
        val branchPattern: String,
        val branchGrouping: String,
        val branchLimit: Int,
        val branchOrdering: String,
        var promotion: String
)