package net.nemerosa.ontrack.extension.sonarqube.casc

import net.nemerosa.ontrack.extension.casc.entities.AbstractCascEntityPropertyContext
import net.nemerosa.ontrack.extension.sonarqube.property.SonarQubeProperty
import net.nemerosa.ontrack.extension.sonarqube.property.SonarQubePropertyType
import net.nemerosa.ontrack.model.structure.PropertyService
import org.springframework.stereotype.Component

@Component
class SonarQubePropertyCascEntity(
    propertyType: SonarQubePropertyType,
    propertyService: PropertyService,
) : AbstractCascEntityPropertyContext<SonarQubeProperty>(
    field = "sonarQubeProperty",
    propertyType = propertyType,
    propertyService = propertyService
)