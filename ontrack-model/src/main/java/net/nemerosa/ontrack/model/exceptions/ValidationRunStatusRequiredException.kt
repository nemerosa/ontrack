package net.nemerosa.ontrack.model.exceptions

class ValidationRunStatusRequiredException(name: String) : InputException(
        "Status is required for validation stamp $name."
)
