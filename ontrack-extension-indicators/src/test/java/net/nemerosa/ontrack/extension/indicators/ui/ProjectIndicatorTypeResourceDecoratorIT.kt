package net.nemerosa.ontrack.extension.indicators.ui

import net.nemerosa.ontrack.extension.indicators.acl.IndicatorRoleContributor
import net.nemerosa.ontrack.extension.indicators.acl.IndicatorTypeManagement
import net.nemerosa.ontrack.extension.indicators.model.IndicatorCategory
import net.nemerosa.ontrack.extension.indicators.model.IndicatorSource
import net.nemerosa.ontrack.extension.indicators.model.IndicatorSourceProviderDescription
import net.nemerosa.ontrack.ui.resource.AbstractResourceDecoratorTestSupport
import net.nemerosa.ontrack.ui.resource.Link
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

class ProjectIndicatorTypeResourceDecoratorIT : AbstractResourceDecoratorTestSupport() {

    @Autowired
    private lateinit var decorator: ProjectIndicatorTypeResourceDecorator

    @Test
    fun `Update and deletion not allowed when source is present even if allowed`() {
        val type = ProjectIndicatorType("test", "Testing", null, category, source, false, null)
        asAdmin {
            type.decorate(decorator) {
                assertLinkNotPresent(Link.UPDATE)
                assertLinkNotPresent(Link.DELETE)
            }
        }
    }

    @Test
    fun `Update and deletion not allowed when not allowed`() {
        val type = ProjectIndicatorType("test", "Testing", null, category, null, false, null)
        asUser().call {
            type.decorate(decorator) {
                assertLinkNotPresent(Link.UPDATE)
                assertLinkNotPresent(Link.DELETE)
            }
        }
    }

    @Test
    fun `Update and deletion allowed when admin`() {
        val type = ProjectIndicatorType("test", "Testing", null, category, null, false, null)
        asAdmin {
            type.decorate(decorator) {
                assertLinkPresent(Link.UPDATE)
                assertLinkPresent(Link.DELETE)
            }
        }
    }

    @Test
    fun `Update and deletion allowed when function granted`() {
        val type = ProjectIndicatorType("test", "Testing", null, category, null, false, null)
        asUserWith<IndicatorTypeManagement> {
            type.decorate(decorator) {
                assertLinkPresent(Link.UPDATE)
                assertLinkPresent(Link.DELETE)
            }
        }
    }

    @Test
    fun `Update and deletion allowed when role granted`() {
        val type = ProjectIndicatorType("test", "Testing", null, category, null, false, null)
        asAccountWithGlobalRole(IndicatorRoleContributor.GLOBAL_INDICATOR_MANAGER) {
            type.decorate(decorator) {
                assertLinkPresent(Link.UPDATE)
                assertLinkPresent(Link.DELETE)
            }
        }
    }

    @Test
    fun `Deletion not granted when type has a source and no deprecation reason`() {
        val type = ProjectIndicatorType("test", "Testing", null, category, source, false, null)
        asAdmin {
            type.decorate(decorator) {
                assertLinkNotPresent(Link.UPDATE)
                assertLinkNotPresent(Link.DELETE)
            }
        }
    }

    @Test
    fun `Deletion not granted when type has a source and a blank deprecation reason`() {
        val type = ProjectIndicatorType("test", "Testing", null, category, source, false, "")
        asAdmin {
            type.decorate(decorator) {
                assertLinkNotPresent(Link.UPDATE)
                assertLinkNotPresent(Link.DELETE)
            }
        }
    }

    @Test
    fun `Deletion granted when type has a source and a deprecation reason`() {
        val type = ProjectIndicatorType("test", "Testing", null, category, source, false, "Ok to go")
        asAdmin {
            type.decorate(decorator) {
                assertLinkNotPresent(Link.UPDATE)
                assertLinkPresent(Link.DELETE)
            }
        }
    }

    @Test
    fun `Deletion granted when type has no source`() {
        val type = ProjectIndicatorType("test", "Testing", null, category, null, false, null)
        asAdmin {
            type.decorate(decorator) {
                assertLinkPresent(Link.UPDATE)
                assertLinkPresent(Link.DELETE)
            }
        }
    }

    private val category = IndicatorCategory(
            id = "test",
            name = "Test",
            source = null
    )

    private val source = IndicatorSource(
            IndicatorSourceProviderDescription("test", "Test"),
            "testing"
    )

}