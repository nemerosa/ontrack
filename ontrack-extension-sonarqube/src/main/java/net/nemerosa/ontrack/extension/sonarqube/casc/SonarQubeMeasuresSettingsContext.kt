package net.nemerosa.ontrack.extension.sonarqube.casc

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.casc.context.settings.AbstractSubSettingsContext
import net.nemerosa.ontrack.extension.casc.schema.*
import net.nemerosa.ontrack.extension.sonarqube.measures.SonarQubeMeasuresSettings
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.settings.SettingsManagerService
import org.springframework.stereotype.Component

@Component
class SonarQubeMeasuresSettingsContext(
    settingsManagerService: SettingsManagerService,
    cachedSettingsService: CachedSettingsService,
) : AbstractSubSettingsContext<SonarQubeMeasuresSettings>(
    "sonarqube-measures",
    SonarQubeMeasuresSettings::class,
    settingsManagerService,
    cachedSettingsService
) {
    override val type: CascType = cascObject(
        "SonarQube measures settings",
        cascField(SonarQubeMeasuresSettings::measures,
            cascArray(
                "List of SonarQube measures",
                cascString
            )
        ),
        cascField(SonarQubeMeasuresSettings::disabled, required = false),
        cascField(SonarQubeMeasuresSettings::coverageThreshold, required = false),
        cascField(SonarQubeMeasuresSettings::blockerThreshold, required = false),
    )

    override fun adjustNodeBeforeParsing(settings: SonarQubeMeasuresSettings, node: JsonNode): JsonNode =
        node.ifMissing(
            settings::disabled,
            settings::coverageThreshold,
            settings::blockerThreshold,
        )
}