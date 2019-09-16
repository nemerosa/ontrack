package net.nemerosa.ontrack.extension.sonarqube.measures

import net.nemerosa.ontrack.extension.sonarqube.SonarQubeMeasuresList

/**
 * List of measures to collect, configured globally.
 *
 * @property measures List of measures to collect
 */
class SonarQubeMeasuresSettings(
        override val measures: List<String>
) : SonarQubeMeasuresList {

    companion object {
        val DEFAULT_MEASURES = listOf(
                "critical_violations",
                "coverage"
        )
    }

}