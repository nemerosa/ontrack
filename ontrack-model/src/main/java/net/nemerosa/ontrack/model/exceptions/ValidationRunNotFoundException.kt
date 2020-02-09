package net.nemerosa.ontrack.model.exceptions

import net.nemerosa.ontrack.model.structure.ID

class ValidationRunNotFoundException(id: ID) : NotFoundException("Validation run ID not found: $id")
