package net.nemerosa.ontrack.extension.sonarqube.measures

import net.nemerosa.ontrack.extension.sonarqube.SonarQubeMeasuresList

/**
 * List of measures to collect, configured globally.
 *
 * @property measures List of measures to collect
 */
class SonarQubeMeasuresSettings(
        override val measures: List<String>,
        val disabled: Boolean,
        val coverageThreshold: Int,
        val blockerThreshold: Int
) : SonarQubeMeasuresList {

    companion object {
        val BLOCKER_VIOLATIONS = "blocker_violations"
        val COVERAGE = "coverage"

        val DEFAULT_MEASURES = listOf(
                BLOCKER_VIOLATIONS,
                COVERAGE
        )

    }

}