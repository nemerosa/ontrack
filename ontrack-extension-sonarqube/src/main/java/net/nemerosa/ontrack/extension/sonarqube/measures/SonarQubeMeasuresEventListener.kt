package net.nemerosa.ontrack.extension.sonarqube.measures

import net.nemerosa.ontrack.extension.sonarqube.property.SonarQubeProperty
import net.nemerosa.ontrack.extension.sonarqube.property.SonarQubePropertyType
import net.nemerosa.ontrack.model.events.Event
import net.nemerosa.ontrack.model.events.EventFactory
import net.nemerosa.ontrack.model.events.EventListener
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import net.nemerosa.ontrack.model.structure.PropertyService
import net.nemerosa.ontrack.model.structure.ValidationStamp
import org.springframework.stereotype.Component

@Component
class SonarQubeMeasuresEventListener(
        private val propertyService: PropertyService,
        private val sonarQubeMeasuresCollectionService: SonarQubeMeasuresCollectionService,
        private val cachedSettingsService: CachedSettingsService,
) : EventListener {
    override fun onEvent(event: Event) {
        if (event.eventType == EventFactory.NEW_VALIDATION_RUN) {
            // Gets the build
            val build: Build = event.getEntity(ProjectEntityType.BUILD)
            // Gets the project & the SonarQube property
            val project = build.project
            val property: SonarQubeProperty? = propertyService.getPropertyValue(project, SonarQubePropertyType::class.java)
            if (property != null) {
                // Gets the validation stamp
                val stamp: ValidationStamp = event.getEntity(ProjectEntityType.VALIDATION_STAMP)
                // Checks the validation stamp
                if (stamp.name == property.validationStamp) {
                    // Filtering on the branch
                    if (sonarQubeMeasuresCollectionService.matches(build, property)) {
                        // Checking the settings
                        val settings = cachedSettingsService.getCachedSettings(SonarQubeMeasuresSettings::class.java)
                        if (!settings.disabled) {
                            // Launching the collection of metrics
                            sonarQubeMeasuresCollectionService.collect(build, property)
                        }
                    }
                }
            }
        }
    }
}