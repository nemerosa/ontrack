package net.nemerosa.ontrack.extension.stale

import net.nemerosa.ontrack.extension.casc.entities.AbstractCascEntityPropertyContext
import net.nemerosa.ontrack.model.structure.PropertyService
import org.springframework.stereotype.Component

@Component
class StalePropertyCascEntity(
    propertyType: StalePropertyType,
    propertyService: PropertyService,
) : AbstractCascEntityPropertyContext<StaleProperty>(
    field = "staleProperty",
    propertyType = propertyType,
    propertyService = propertyService
)