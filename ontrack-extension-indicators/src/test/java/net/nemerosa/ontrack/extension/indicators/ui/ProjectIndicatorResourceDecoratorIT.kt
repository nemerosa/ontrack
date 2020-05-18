package net.nemerosa.ontrack.extension.indicators.ui

import net.nemerosa.ontrack.extension.indicators.acl.IndicatorEdit
import net.nemerosa.ontrack.extension.indicators.acl.IndicatorRoleContributor
import net.nemerosa.ontrack.extension.indicators.model.*
import net.nemerosa.ontrack.extension.indicators.values.BooleanIndicatorValueType
import net.nemerosa.ontrack.extension.indicators.values.BooleanIndicatorValueTypeConfig
import net.nemerosa.ontrack.model.structure.Signature
import net.nemerosa.ontrack.ui.resource.AbstractResourceDecoratorTestSupport
import net.nemerosa.ontrack.ui.resource.Link
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

class ProjectIndicatorResourceDecoratorIT : AbstractResourceDecoratorTestSupport() {

    @Autowired
    private lateinit var decorator: ProjectIndicatorResourceIndicator

    @Autowired
    private lateinit var valueType: BooleanIndicatorValueType

    @Test
    fun `Update and deletion not allowed when source is present even if allowed`() {
        val indicator = indicator(source)
        asAdmin {
            indicator.decorate(decorator) {
                assertLinkNotPresent(Link.UPDATE)
                assertLinkNotPresent(Link.DELETE)
            }
        }
    }

    @Test
    fun `Update and deletion not allowed when not allowed`() {
        val indicator = indicator()
        asUser().call {
            indicator.decorate(decorator) {
                assertLinkNotPresent(Link.UPDATE)
                assertLinkNotPresent(Link.DELETE)
            }
        }
    }

    @Test
    fun `Update and deletion allowed when admin`() {
        val indicator = indicator()
        asAdmin {
            indicator.decorate(decorator) {
                assertLinkPresent(Link.UPDATE)
                assertLinkPresent(Link.DELETE)
            }
        }
    }

    @Test
    fun `Update and deletion allowed when function granted`() {
        val indicator = indicator()
        asUser().with(indicator.project, IndicatorEdit::class.java).call {
            indicator.decorate(decorator) {
                assertLinkPresent(Link.UPDATE)
                assertLinkPresent(Link.DELETE)
            }
        }
    }

    @Test
    fun `Update and deletion allowed when global role granted`() {
        val indicator = indicator()
        asAccountWithGlobalRole(IndicatorRoleContributor.GLOBAL_INDICATOR_MANAGER) {
            indicator.decorate(decorator) {
                assertLinkPresent(Link.UPDATE)
                assertLinkPresent(Link.DELETE)
            }
        }
    }

    @Test
    fun `Update and deletion allowed when project role granted`() {
        val indicator = indicator()
        indicator.project.asAccountWithProjectRole(IndicatorRoleContributor.PROJECT_INDICATOR_MANAGER) {
            indicator.decorate(decorator) {
                assertLinkPresent(Link.UPDATE)
                assertLinkPresent(Link.DELETE)
            }
        }
    }

    private fun indicator(source: IndicatorSource? = null) = project<ProjectIndicator> {
        ProjectIndicator(
                project = this,
                indicator = Indicator(
                        type = type(source),
                        value = true,
                        compliance = null,
                        comment = null,
                        signature = Signature.anonymous()
                )
        )
    }

    private fun type(source: IndicatorSource? = null) =
            IndicatorType(
                    id = "test",
                    category = category,
                    name = "Testing",
                    link = null,
                    valueType = valueType,
                    valueConfig = BooleanIndicatorValueTypeConfig(required = false),
                    valueComputer = null,
                    source = source
            )

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