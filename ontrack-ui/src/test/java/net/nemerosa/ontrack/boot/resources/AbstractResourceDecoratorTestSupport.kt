package net.nemerosa.ontrack.boot.resources

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.ui.controller.MockURIBuilder
import net.nemerosa.ontrack.ui.resource.DefaultResourceContext
import net.nemerosa.ontrack.ui.resource.Link
import net.nemerosa.ontrack.ui.resource.ResourceContext
import net.nemerosa.ontrack.ui.resource.ResourceDecorator
import org.junit.Before
import kotlin.test.assertTrue

abstract class AbstractResourceDecoratorTestSupport : AbstractDSLTestSupport() {

    private lateinit var resourceContext: ResourceContext

    @Before
    fun initResourceContext() {
        resourceContext = DefaultResourceContext(
                MockURIBuilder(),
                securityService
        )
    }

    protected fun <T> T.decorate(decorator: ResourceDecorator<T>, code: List<Link>.() -> Unit): List<Link> {
        val links = decorator.links(this, resourceContext)
        links.code()
        return links
    }

    protected fun List<Link>.assertLinkPresent(name: String) {
        assertTrue(
                any { link -> link.name == name },
                "Link with name $name is present"
        )
    }

    protected fun List<Link>.assertLinkNotPresent(name: String) {
        assertTrue(
                none { link -> link.name == name },
                "Link with name $name is not present"
        )
    }

}