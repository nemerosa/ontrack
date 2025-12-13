package net.nemerosa.ontrack.extension.scm.branching

import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.support.NameValue

class BranchingModelProperty(
    @APIDescription("List of branch patterns (name & value). The name is the category of branch and the value is a regular expression on the SCM branch.")
    val patterns: List<NameValue>
)