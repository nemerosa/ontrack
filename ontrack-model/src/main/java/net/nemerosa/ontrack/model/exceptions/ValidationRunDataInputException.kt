package net.nemerosa.ontrack.model.exceptions

open class ValidationRunDataInputException(pattern: String) : InputException(pattern)

class ValidationRunDataStatusRequiredException : ValidationRunDataInputException(
        "Validation Run Status is required."
)

class ValidationRunDataFormatException(message: String) : ValidationRunDataInputException(message)

class ValidationRunDataMismatchException(
        actualId: String,
        expectedId: String
) : ValidationRunDataInputException(
        "Data associated with the validation run as different " +
                "type than the one associated with the validation stamp. " +
                "`$expectedId` is expected and `$actualId` was given."

)

class ValidationRunDataTypeNotFoundException(type: String) : ValidationRunDataInputException(
        "Cannot find any data type for ID `$type`"
)
