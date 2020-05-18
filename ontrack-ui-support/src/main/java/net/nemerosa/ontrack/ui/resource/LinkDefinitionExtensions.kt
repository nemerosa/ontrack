package net.nemerosa.ontrack.ui.resource

import net.nemerosa.ontrack.model.security.GlobalFunction
import net.nemerosa.ontrack.model.security.ProjectFunction
import net.nemerosa.ontrack.model.structure.ProjectEntity
import kotlin.reflect.KClass

infix fun <T> String.linkTo(linkFn: (T) -> Any): LinkDefinition<T> = linkTo { t, _ -> linkFn(t) }

infix fun <T> String.linkTo(linkFn: (T, ResourceContext) -> Any): LinkDefinition<T> =
        SimpleLinkDefinition(this, linkFn) { _, _ -> true }

infix fun <T : ProjectEntity, P : ProjectFunction> LinkDefinition<T>.linkIf(fn: KClass<P>): LinkDefinition<T> = addCheckFn { t, rc ->
    rc.isProjectFunctionGranted(t, fn.java)
}

infix fun <T, G : GlobalFunction> LinkDefinition<T>.linkIfGlobal(fn: KClass<G>): LinkDefinition<T> = addCheckFn { _, rc ->
    rc.isGlobalFunctionGranted(fn.java)
}

infix fun <T> LinkDefinition<T>.linkIf(condition: (T, ResourceContext) -> Boolean): LinkDefinition<T> = addCheckFn(condition)

private fun <T> LinkDefinition<T>.addCheckFn(fn: (T, ResourceContext) -> Boolean) = object : LinkDefinition<T> {
    override val name: String = this@addCheckFn.name
    override val checkFn: (T, ResourceContext) -> Boolean = fn
    override fun addLink(linksBuilder: LinksBuilder, resource: T, resourceContext: ResourceContext): LinksBuilder =
            this@addCheckFn.addLink(linksBuilder, resource, resourceContext)
}