package net.nemerosa.ontrack.model.exceptions

import net.nemerosa.ontrack.model.structure.ID

class PromotionRunNotFoundException(id: ID) : NotFoundException("Promotion run ID not found: $id")
