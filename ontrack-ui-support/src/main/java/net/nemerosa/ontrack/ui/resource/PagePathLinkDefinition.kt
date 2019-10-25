package net.nemerosa.ontrack.ui.resource

import net.nemerosa.ontrack.model.structure.ProjectEntity
import java.util.function.BiFunction
import java.util.function.BiPredicate

class PagePathLinkDefinition<T : ProjectEntity>(
        override val name: String,
        val pathFn: (T, ResourceContext) -> String,
        override val checkFn: (T, ResourceContext) -> Boolean
) : LinkDefinition<T> {

    @Deprecated("Use Kotlin functions")
    constructor(name: String, pathFn: BiFunction<T, ResourceContext, String>, checkFn: BiPredicate<T, ResourceContext>) : this(
            name,
            pathFn::apply,
            checkFn::test
    )

    override fun addLink(linksBuilder: LinksBuilder, resource: T, resourceContext: ResourceContext): LinksBuilder {
        return linksBuilder.page(
                name,
                checkFn(resource, resourceContext),
                pathFn(resource, resourceContext)
        )
    }

}
