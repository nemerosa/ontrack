package net.nemerosa.ontrack.model.structure

import net.nemerosa.ontrack.model.exceptions.ValidationRunDataInputException
import net.nemerosa.ontrack.model.extension.ExtensionFeature

abstract class AbstractValidationDataType<C, T>(
        override val feature: ExtensionFeature
) : ValidationDataType<C, T> {

    protected fun validateNotNull(data: T?): T {
        if (data == null) {
            throw ValidationRunDataInputException("Data must not be null")
        } else {
            return data
        }
    }

    protected fun validateNotNull(data: T?, validation: T.() -> Unit): T {
        if (data == null) {
            throw ValidationRunDataInputException("Data must not be null")
        } else {
            validation(data)
            return data
        }
    }

    protected fun validate(check: Boolean, message: String) {
        if (!check) {
            throw ValidationRunDataInputException(message)
        }
    }

}
