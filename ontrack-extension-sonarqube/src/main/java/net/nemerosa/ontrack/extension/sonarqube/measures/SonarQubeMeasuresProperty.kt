package net.nemerosa.ontrack.extension.sonarqube.measures

/**
 * List of measures to collect.
 *
 * @property measures List of measures to collect
 * @property override True if this list overrides more global settings
 */
class SonarQubeMeasuresProperty(
        val override: Boolean,
        override val measures: List<String>
) : SonarQubeMeasuresList
