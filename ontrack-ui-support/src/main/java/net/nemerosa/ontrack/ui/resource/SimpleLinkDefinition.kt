package net.nemerosa.ontrack.ui.resource

import java.util.function.BiFunction
import java.util.function.BiPredicate

class SimpleLinkDefinition<T>(
        override val name: String,
        val linkFn: (T, ResourceContext) -> Any,
        override val checkFn: (T, ResourceContext) -> Boolean
) : LinkDefinition<T> {

    @Deprecated("Use Kotlin functions")
    constructor(name: String, linkFn: BiFunction<T, ResourceContext, Any>, checkFn: BiPredicate<T, ResourceContext>) : this(
            name,
            linkFn::apply,
            checkFn::test
    )

    override fun addLink(linksBuilder: LinksBuilder, resource: T, resourceContext: ResourceContext): LinksBuilder {
        return linksBuilder.link(
                name,
                linkFn(resource, resourceContext)
        )
    }

}
