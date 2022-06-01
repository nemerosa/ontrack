package net.nemerosa.ontrack.extension.av.property

import net.nemerosa.ontrack.model.exceptions.InputException

/**
 * Exception thrown out if the auto versioning configuration misses either
 * the target regex or the target property.
 */
class MissingTargetRegexOrPropertyException : InputException(
    "Either the target regex or the target property must be defined"
)
