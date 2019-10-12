package net.nemerosa.ontrack.model.exceptions

import net.nemerosa.ontrack.model.structure.ID

class ValidationRunStatusIDNotAvailableException(id: ID) : NotFoundException("Validation run status ID=$id cannot be found or is not available for your rights.") {
}