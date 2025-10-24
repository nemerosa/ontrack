package net.nemerosa.ontrack.service.json.schema

import net.nemerosa.ontrack.model.exceptions.NotFoundException

class JsonSchemaDefinitionNotFoundException(id: String) : NotFoundException(
    """JSON schema definition cannot be found: $id"""
)