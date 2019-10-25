package net.nemerosa.ontrack.ui.resource

interface LinkDefinition<T> {

    val name: String

    val checkFn: (T, ResourceContext) -> Boolean

    fun addLink(linksBuilder: LinksBuilder, resource: T, resourceContext: ResourceContext): LinksBuilder

}
