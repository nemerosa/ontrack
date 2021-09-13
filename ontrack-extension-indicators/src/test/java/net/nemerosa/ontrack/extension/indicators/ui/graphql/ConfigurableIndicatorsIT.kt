package net.nemerosa.ontrack.extension.indicators.ui.graphql

import net.nemerosa.ontrack.extension.api.support.TestExtensionFeature
import net.nemerosa.ontrack.extension.indicators.AbstractIndicatorsTestSupport
import net.nemerosa.ontrack.extension.indicators.computing.*
import net.nemerosa.ontrack.extension.indicators.model.IndicatorSource
import net.nemerosa.ontrack.extension.indicators.model.IndicatorSourceProviderDescription
import net.nemerosa.ontrack.extension.indicators.values.BooleanIndicatorValueType
import net.nemerosa.ontrack.extension.indicators.values.BooleanIndicatorValueTypeConfig
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import kotlin.test.assertEquals

/**
 * Testing the management of configurable indicators through the GraphQL API.
 */
class ConfigurableIndicatorsIT : AbstractIndicatorsTestSupport() {

    @Autowired
    private lateinit var configurableIndicatorService: ConfigurableIndicatorService

    @Test
    fun `Getting the list of configurable indicators and their current state`() {
        // Configurable indicator to use
        val configurableIndicatorType = testConfigurableIndicatorComputer.configurableIndicators.first()
        // Saves the state of a configurable indicator
        configurableIndicatorType.save(
            enabled = true,
            link = null,
            values = mapOf("length" to "12")
        )
        // Gets the (filtered) list of configurable indicators
        run(
            """
           {
                configurableIndicators(type: "${configurableIndicatorType.id}") {
                    category {
                        id
                        name
                    }
                    id
                    name
                    attributes {
                        key
                        name
                        type
                        required
                    }
                    state {
                        enabled
                        link
                        values {
                            key
                            value
                        }
                    }
                }
           }
        """
        ).let { data ->
            assertEquals(
                mapOf(
                    "configurableIndicators" to listOf(
                        mapOf(
                            "category" to mapOf(
                                "id" to "testing",
                                "name" to "Testing"
                            ),
                            "id" to configurableIndicatorType.id,
                            "name" to configurableIndicatorType.name,
                            "attributes" to listOf(
                                mapOf(
                                    "key" to "length",
                                    "name" to "Min length of project name",
                                    "type" to "INT",
                                    "required" to true
                                )
                            ),
                            "state" to mapOf(
                                "enabled" to true,
                                "link" to null,
                                "values" to listOf(
                                    mapOf(
                                        "key" to "length",
                                        "value" to "12"
                                    )
                                )
                            )
                        )
                    )
                ).asJson(),
                data
            )
        }
    }

    /**
     * Indicator computer based on some configurable indicators.
     */
    @Component
    class TestConfigurableIndicatorComputer(
        extensionFeature: TestExtensionFeature,
        configurableIndicatorService: ConfigurableIndicatorService,
        booleanIndicatorValueType: BooleanIndicatorValueType,
    ) : AbstractConfigurableIndicatorComputer(extensionFeature, configurableIndicatorService) {
        override val name: String = "TestConfigurableIndicatorComputer"
        override val perProject: Boolean = true
        override val source = IndicatorSource(
            provider = IndicatorSourceProviderDescription("test", "Testing"),
            name = "Test configurable indicators"
        )

        override fun isProjectEligible(project: Project): Boolean = true

        override val configurableIndicators: List<ConfigurableIndicatorType<*, *>> = listOf(
            ConfigurableIndicatorType(
                category = IndicatorComputedCategory("testing", "Testing"),
                id = uid("cit"),
                name = "TestConfigurableIndicator",
                valueType = booleanIndicatorValueType,
                valueConfig = { _, _ -> BooleanIndicatorValueTypeConfig(required = false) },
                attributes = listOf(
                    ConfigurableIndicatorAttribute(
                        key = "length",
                        name = "Min length of project name",
                        required = true,
                        type = ConfigurableIndicatorAttributeType.INT,
                    )
                ),
                computing = { project, state ->
                    val length = state.getIntAttribute("length")
                    if (length != null) {
                        val name = project.name
                        name.length <= length
                    } else {
                        null
                    }
                }
            )
        )
    }

    @Autowired
    private lateinit var testConfigurableIndicatorComputer: TestConfigurableIndicatorComputer

    private fun ConfigurableIndicatorType<*, *>.save(
        enabled: Boolean,
        link: String?,
        values: Map<String, String?>
    ) {
        configurableIndicatorService.saveConfigurableIndicator(
            this,
            ConfigurableIndicatorState(
                enabled = enabled,
                link = link,
                values = ConfigurableIndicatorState.toAttributeList(this, values)
            )
        )
    }

}