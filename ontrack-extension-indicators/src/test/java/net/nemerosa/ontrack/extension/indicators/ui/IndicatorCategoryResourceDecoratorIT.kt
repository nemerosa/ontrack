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

class IndicatorCategoryResourceDecoratorIT : AbstractResourceDecoratorTestSupport() {

    @Autowired
    private lateinit var decorator: IndicatorCategoryResourceDecorator

    @Test
    fun `Update and deletion not allowed when source is present even if allowed`() {
        val category = IndicatorCategory("test", "Test", source)
        asAdmin {
            category.decorate(decorator) {
                assertLinkNotPresent(Link.UPDATE)
                assertLinkNotPresent(Link.DELETE)
            }
        }
    }

    @Test
    fun `Update and deletion not allowed when not allowed`() {
        val category = IndicatorCategory("test", "Test", null)
        asUser().call {
            category.decorate(decorator) {
                assertLinkNotPresent(Link.UPDATE)
                assertLinkNotPresent(Link.DELETE)
            }
        }
    }

    @Test
    fun `Update and deletion allowed when admin`() {
        val category = IndicatorCategory("test", "Test", null)
        asAdmin {
            category.decorate(decorator) {
                assertLinkPresent(Link.UPDATE)
                assertLinkPresent(Link.DELETE)
            }
        }
    }

    @Test
    fun `Update and deletion allowed when function granted`() {
        val category = IndicatorCategory("test", "Test", null)
        asUserWith<IndicatorTypeManagement> {
            category.decorate(decorator) {
                assertLinkPresent(Link.UPDATE)
                assertLinkPresent(Link.DELETE)
            }
        }
    }

    @Test
    fun `Update and deletion allowed when role granted`() {
        val category = IndicatorCategory("test", "Test", null)
        asAccountWithGlobalRole(IndicatorRoleContributor.GLOBAL_INDICATOR_MANAGER) {
            category.decorate(decorator) {
                assertLinkPresent(Link.UPDATE)
                assertLinkPresent(Link.DELETE)
            }
        }
    }

    private val source = IndicatorSource(
            IndicatorSourceProviderDescription("test", "Test"),
            "testing"
    )

}