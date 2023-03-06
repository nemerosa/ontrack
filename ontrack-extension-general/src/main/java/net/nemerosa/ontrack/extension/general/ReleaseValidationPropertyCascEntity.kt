package net.nemerosa.ontrack.extension.general

import net.nemerosa.ontrack.extension.casc.entities.AbstractCascEntityPropertyContext
import net.nemerosa.ontrack.model.structure.PropertyService
import net.nemerosa.ontrack.model.structure.PropertyType
import org.springframework.stereotype.Component

/**
 * Setting the [ReleaseValidationProperty] property on a branch using Casc.
 */
@Component
class ReleaseValidationPropertyCascEntity(
    propertyType: PropertyType<ReleaseValidationProperty>,
    propertyService: PropertyService,
) : AbstractCascEntityPropertyContext<ReleaseValidationProperty>(
    field = "releaseValidationProperty",
    propertyType = propertyType,
    propertyService = propertyService
)
