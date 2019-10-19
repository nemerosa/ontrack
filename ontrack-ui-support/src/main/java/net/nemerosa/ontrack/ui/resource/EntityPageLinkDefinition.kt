package net.nemerosa.ontrack.ui.resource

import net.nemerosa.ontrack.model.structure.ProjectEntity

class EntityPageLinkDefinition<T : ProjectEntity>(
        override val checkFn: (T, ResourceContext) -> Boolean
) : LinkDefinition<T> {

    override val name: String = Link.PAGE

    override fun addLink(linksBuilder: LinksBuilder, resource: T, resourceContext: ResourceContext): LinksBuilder {
        return linksBuilder.page(resource)
    }

}
