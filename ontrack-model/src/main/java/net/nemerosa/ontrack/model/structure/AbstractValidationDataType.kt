package net.nemerosa.ontrack.model.structure

import net.nemerosa.ontrack.model.exceptions.ValidationRunDataInputException
import net.nemerosa.ontrack.model.extension.ExtensionFeature

abstract class AbstractValidationDataType<C, T>(
        private val extensionFeature: ExtensionFeature
) : ValidationDataType<C, T> {

    override fun getFeature() = extensionFeature

    protected fun validate(check: Boolean, message: String) {
        if (!check) {
            throw ValidationRunDataInputException(message)
        }
    }

}
