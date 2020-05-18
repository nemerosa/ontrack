package net.nemerosa.ontrack.extension.sonarqube.indicators

import net.nemerosa.ontrack.extension.indicators.computing.AbstractBranchIndicatorComputer
import net.nemerosa.ontrack.extension.indicators.model.*
import net.nemerosa.ontrack.extension.indicators.support.IntegerThresholds
import net.nemerosa.ontrack.extension.indicators.support.PercentageThreshold
import net.nemerosa.ontrack.extension.indicators.support.percent
import net.nemerosa.ontrack.extension.indicators.values.IntegerIndicatorValueType
import net.nemerosa.ontrack.extension.indicators.values.PercentageIndicatorValueType
import net.nemerosa.ontrack.extension.sonarqube.SonarQubeExtensionFeature
import net.nemerosa.ontrack.extension.sonarqube.measures.SonarQubeMeasuresCollectionService
import net.nemerosa.ontrack.extension.sonarqube.measures.SonarQubeMeasuresSettings
import net.nemerosa.ontrack.extension.sonarqube.property.SonarQubePropertyType
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.PropertyService
import net.nemerosa.ontrack.model.structure.StructureService
import org.springframework.stereotype.Component

@Component
class SonarQubeIndicatorComputer(
        extension: SonarQubeExtensionFeature,
        structureService: StructureService,
        sonarQubeIndicatorSourceProvider: SonarQubeIndicatorSourceProvider,
        private val propertyService: PropertyService,
        private val percentageIndicatorValueType: PercentageIndicatorValueType,
        private val integerIndicatorValueType: IntegerIndicatorValueType,
        private val sonarQubeMeasuresCollectionService: SonarQubeMeasuresCollectionService,
        private val cachedSettingsService: CachedSettingsService
) : AbstractBranchIndicatorComputer(extension, structureService) {

    override val name: String = "SonarQube metric indicators"

    override val perProject = true

    override val source: IndicatorSource = sonarQubeIndicatorSourceProvider.createSource("")

    override fun isBranchEligible(branch: Branch): Boolean =
            propertyService.hasProperty(branch.project, SonarQubePropertyType::class.java)

    override fun computeIndicators(branch: Branch): List<IndicatorComputedValue<*, *>> {
        val measures = sonarQubeMeasuresCollectionService.getLastMeasures(branch)
        return sonarTypes.map { sonarType ->
            val measure = measures?.measures?.get(sonarType.measure)
            toIndicator(sonarType, measure)
        }
    }

    private fun <T> toIndicator(sonarType: SonarQubeIndicatorType<T>, measure: Double?): IndicatorComputedValue<T, *> {
        val value = measure?.let { sonarType.toValueConverter(it) }
        return IndicatorComputedValue(
                type = sonarType.type,
                value = value,
                comment = null
        )
    }

    private val sonarCategory = IndicatorComputedCategory(
            id = "sonarqube",
            name = "SonarQube metrics"
    )

    private val sonarTypes: List<SonarQubeIndicatorType<*>> get() {
        val settings = cachedSettingsService.getCachedSettings(SonarQubeMeasuresSettings::class.java)
        return listOf(
                SonarQubeIndicatorType(
                        measure = SonarQubeMeasuresSettings.COVERAGE,
                        type = IndicatorComputedType(
                                category = sonarCategory,
                                id = "sonarqube-coverage",
                                name = "SonarQube coverage",
                                link = null,
                                valueType = percentageIndicatorValueType,
                                valueConfig = PercentageThreshold(threshold = settings.coverageThreshold.percent(), higherIsBetter = true)
                        ),
                        toValueConverter = { m ->
                            when {
                                m < 0 -> 0.percent()
                                m > 100 -> 100.percent()
                                else -> m.toInt().percent()
                            }
                        }
                ),
                SonarQubeIndicatorType(
                        measure = SonarQubeMeasuresSettings.BLOCKER_VIOLATIONS,
                        type = IndicatorComputedType(
                                category = sonarCategory,
                                id = "sonarqube-blocker-violations",
                                name = "SonarQube blocker issues",
                                link = null,
                                valueType = integerIndicatorValueType,
                                valueConfig = IntegerThresholds(
                                        min = 0,
                                        max = settings.blockerThreshold,
                                        higherIsBetter = false
                                )
                        ),
                        toValueConverter = { m -> m.toInt() }
                )
        )
    }

    class SonarQubeIndicatorType<T>(
            val measure: String,
            val type: IndicatorComputedType<T, *>,
            val toValueConverter: (Double) -> T
    )
}