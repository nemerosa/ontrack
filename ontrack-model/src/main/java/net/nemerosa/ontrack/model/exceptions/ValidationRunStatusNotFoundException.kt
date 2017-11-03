package net.nemerosa.ontrack.model.exceptions

class ValidationRunStatusNotFoundException(status: String) : NotFoundException(
        "Status [$status] is not defined."
)
