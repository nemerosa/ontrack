package net.nemerosa.ontrack.model.templating

import net.nemerosa.ontrack.model.structure.ProjectEntityType

abstract class AbstractTemplatingSource(
    override val field: String,
    private val types: Set<ProjectEntityType>,
) : TemplatingSource {

    constructor(
        field: String,
        type: ProjectEntityType,
    ) : this(
        field = field,
        types = setOf(type)
    )

    override fun validFor(projectEntityType: ProjectEntityType): Boolean =
        projectEntityType in types

    protected fun Map<String, String>.getRequiredParam(key: String) =
        this[key] ?: throw TemplatingMissingConfigParam(key)

}