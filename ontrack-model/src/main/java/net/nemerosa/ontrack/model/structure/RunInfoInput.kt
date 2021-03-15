package net.nemerosa.ontrack.model.structure

import net.nemerosa.ontrack.model.annotations.APIDescription

class RunInfoInput(
        @APIDescription("Type of source (like \"github\")")
        val sourceType: String? = null,
        @APIDescription("URI to the source of the run (like the URL to a Jenkins job)")
        val sourceUri: String? = null,
        @APIDescription("Type of trigger (like \"scm\" or \"user\")")
        val triggerType: String? = null,
        @APIDescription("Data associated with the trigger (like a user ID or a commit)")
        val triggerData: String? = null,
        @APIDescription("Time of the run (in seconds)")
        val runTime: Int? = null
)
