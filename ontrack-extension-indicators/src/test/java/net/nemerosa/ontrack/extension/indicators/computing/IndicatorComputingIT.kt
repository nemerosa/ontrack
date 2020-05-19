package net.nemerosa.ontrack.extension.indicators.computing

import net.nemerosa.ontrack.extension.indicators.AbstractIndicatorsTestSupport
import net.nemerosa.ontrack.extension.indicators.IndicatorsExtensionFeature
import net.nemerosa.ontrack.extension.indicators.model.*
import net.nemerosa.ontrack.extension.indicators.support.Percentage
import net.nemerosa.ontrack.extension.indicators.support.PercentageThreshold
import net.nemerosa.ontrack.extension.indicators.support.percent
import net.nemerosa.ontrack.extension.indicators.values.BooleanIndicatorValueType
import net.nemerosa.ontrack.extension.indicators.values.BooleanIndicatorValueTypeConfig
import net.nemerosa.ontrack.extension.indicators.values.PercentageIndicatorValueType
import net.nemerosa.ontrack.model.extension.ExtensionFeature
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class IndicatorComputingIT : AbstractIndicatorsTestSupport() {

    @Autowired
    private lateinit var indicatorComputingService: IndicatorComputingService

    @Autowired
    private lateinit var booleanValueType: BooleanIndicatorValueType

    @Autowired
    private lateinit var percentageValueType: PercentageIndicatorValueType

    @Test
    fun `Computed categories and types synchronization`() {
        var computer = testIndicatorComputer(
                coverage = true,
                springBoot = true,
                docker = false
        )
        project {
            asAdmin {
                indicatorComputingService.compute(computer, this)
            }
        }
        // Checks the types have been registered
        computer.assertTypeIsPresent(computer.typeCoverage)
        computer.assertTypeIsPresent(computer.typeSpringBoot)
        computer.assertTypeIsNotPresent(computer.typeDocker)

        // Changing the indicator computer types
        computer = testIndicatorComputer(
                coverage = false,
                springBoot = true,
                docker = true
        )

        // Recomputing (on another project, it does not matter)
        project {
            asAdmin {
                indicatorComputingService.compute(computer, this)
            }
        }

        // Checks the types registration
        computer.assertTypeIsNotPresent(computer.typeCoverage)
        computer.assertTypeIsPresent(computer.typeSpringBoot)
        computer.assertTypeIsPresent(computer.typeDocker)
    }

    @Test
    fun `Creation of project indicators`() {
        var computer = testIndicatorComputer(
                valueCoverage = 70.percent(),
                valueSpringBoot = null,
                valueDocker = null
        )
        project {
            asAdmin {
                indicatorComputingService.compute(computer, this)
                // Checks the indicators
                computer.assertIndicatorValueIs(this, computer.typeCoverage, 70.percent())
                computer.assertIndicatorValueIs(this, computer.typeSpringBoot, null)
                computer.assertIndicatorValueIs(this, computer.typeDocker, null)
                // Adds some types, remove one
                computer = testIndicatorComputer(
                        valueCoverage = null,
                        valueSpringBoot = true,
                        valueDocker = false
                )
                indicatorComputingService.compute(computer, this)
                // Checks the indicators
                computer.assertIndicatorValueIs(this, computer.typeCoverage, null)
                computer.assertIndicatorValueIs(this, computer.typeSpringBoot, true)
                computer.assertIndicatorValueIs(this, computer.typeDocker, false)
            }
        }
    }

    private fun testIndicatorComputer(
            coverage: Boolean = true,
            springBoot: Boolean = true,
            docker: Boolean = true,
            valueCoverage: Percentage? = 70.percent(),
            valueSpringBoot: Boolean? = true,
            valueDocker: Boolean? = true
    ) = TestIndicatorComputer(
            coverage = coverage,
            springBoot = springBoot,
            docker = docker,
            valueCoverage = valueCoverage,
            valueSpringBoot = valueSpringBoot,
            valueDocker = valueDocker
    )

    private inner class TestIndicatorComputer(
            private val coverage: Boolean,
            private val springBoot: Boolean,
            private val docker: Boolean,
            private val valueCoverage: Percentage?,
            private val valueSpringBoot: Boolean?,
            private val valueDocker: Boolean?
    ) : IndicatorComputer {

        private val prefix = uid("C")

        val category = IndicatorComputedCategory(
                id = prefix,
                name = "$prefix category"
        )

        val typeCoverage = IndicatorComputedType(
                id = "$prefix-coverage",
                category = category,
                name = "$prefix coverage",
                link = null,
                valueType = percentageValueType,
                valueConfig = PercentageThreshold(threshold = 80.percent(), higherIsBetter = true)
        )

        val typeSpringBoot = IndicatorComputedType(
                id = "$prefix-spring-boot",
                category = category,
                name = "$prefix Spring Boot",
                link = null,
                valueType = booleanValueType,
                valueConfig = BooleanIndicatorValueTypeConfig(required = false)
        )

        val typeDocker = IndicatorComputedType(
                id = "$prefix-docker",
                category = category,
                name = "$prefix Docker",
                link = null,
                valueType = booleanValueType,
                valueConfig = BooleanIndicatorValueTypeConfig(required = true)
        )

        override val name: String = "Test computer"

        override val perProject: Boolean = true

        override fun isProjectEligible(project: Project): Boolean = true

        override val source: IndicatorSource = IndicatorSource(
                IndicatorSourceProviderDescription("test", "Test"),
                "Testing"
        )

        override fun computeIndicators(project: Project): List<IndicatorComputedValue<*, *>> {

            val indicators = mutableListOf<IndicatorComputedValue<*, *>>()

            if (coverage) {
                indicators += IndicatorComputedValue(
                        type = typeCoverage,
                        value = valueCoverage,
                        comment = null
                )
            }

            if (springBoot) {
                indicators += IndicatorComputedValue(
                        type = typeSpringBoot,
                        value = valueSpringBoot,
                        comment = null
                )
            }

            if (docker) {
                indicators += IndicatorComputedValue(
                        type = typeDocker,
                        value = valueDocker,
                        comment = null
                )
            }

            return indicators.toList()
        }

        override fun getFeature(): ExtensionFeature = IndicatorsExtensionFeature()

        fun assertTypeIsPresent(type: IndicatorComputedType<*, *>) {
            assertNotNull(indicatorTypeService.findTypeById(type.id)) {
                assertEquals(source, it.source)
                assertTrue(it.computed, "Computed flag is ON")
            }
        }

        fun assertTypeIsNotPresent(type: IndicatorComputedType<*, *>) {
            assertNull(indicatorTypeService.findTypeById(type.id))
        }

        fun <T : Any> assertIndicatorValueIs(
                project: Project,
                computerType: IndicatorComputedType<T, *>,
                expectedValue: T?
        ) {
            @Suppress("UNCHECKED_CAST")
            val type = indicatorTypeService.getTypeById(computerType.id) as IndicatorType<T, *>
            project.assertIndicatorValueIs(type, expectedValue)
        }

    }

}