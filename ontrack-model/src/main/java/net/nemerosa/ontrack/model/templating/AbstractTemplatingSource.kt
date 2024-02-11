package net.nemerosa.ontrack.model.templating

import net.nemerosa.ontrack.model.structure.ProjectEntityType

abstract class AbstractTemplatingSource(
    override val field: String,
    override val types: Set<ProjectEntityType>,
) : TemplatingSource {

    constructor(
        field: String,
        type: ProjectEntityType,
    ) : this(
        field = field,
        types = setOf(type)
    )

}