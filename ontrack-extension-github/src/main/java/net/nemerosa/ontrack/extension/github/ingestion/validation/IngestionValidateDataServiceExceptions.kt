package net.nemerosa.ontrack.extension.github.ingestion.validation

import net.nemerosa.ontrack.extension.github.ingestion.processing.model.Repository
import net.nemerosa.ontrack.model.exceptions.NotFoundException

class IngestionValidateDataServiceProjectNotFoundException(repository: Repository) : NotFoundException(
    """Project for ${repository.fullName} not found."""
)
