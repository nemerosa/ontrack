package net.nemerosa.ontrack.extension.casc.entities

import org.springframework.stereotype.Component

@Component
class CascEntityPropertiesContext(
    subContexts: List<CascEntityPropertyContext>,
) : AbstractHolderCascEntityContext<CascEntityPropertyContext>(
    subContexts,
    description = "List of properties") {
}
