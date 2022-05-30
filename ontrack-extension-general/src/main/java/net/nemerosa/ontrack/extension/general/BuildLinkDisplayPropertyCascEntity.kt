package net.nemerosa.ontrack.extension.general

import net.nemerosa.ontrack.extension.casc.entities.AbstractCascEntityPropertyContext
import net.nemerosa.ontrack.model.structure.PropertyService
import net.nemerosa.ontrack.model.structure.PropertyType
import org.springframework.stereotype.Component

@Component
class BuildLinkDisplayPropertyCascEntity(
    propertyType: PropertyType<BuildLinkDisplayProperty>,
    propertyService: PropertyService,
) : AbstractCascEntityPropertyContext<BuildLinkDisplayProperty>(
    field = "buildLinkDisplayProperty",
    propertyType = propertyType,
    propertyService = propertyService
)
