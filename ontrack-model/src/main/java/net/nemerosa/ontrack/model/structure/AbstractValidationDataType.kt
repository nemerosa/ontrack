package net.nemerosa.ontrack.model.structure

import net.nemerosa.ontrack.model.exceptions.ValidationRunDataInputException

abstract class AbstractValidationDataType<C, T> : ValidationDataType<C, T> {

    protected fun validate(check: Boolean, message: String) {
        if (!check) {
            throw ValidationRunDataInputException(message)
        }
    }

}
