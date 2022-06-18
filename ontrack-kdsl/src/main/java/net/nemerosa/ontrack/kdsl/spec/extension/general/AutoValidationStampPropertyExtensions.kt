package net.nemerosa.ontrack.kdsl.spec.extension.general

import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.kdsl.spec.Project
import net.nemerosa.ontrack.kdsl.spec.deleteProperty
import net.nemerosa.ontrack.kdsl.spec.getProperty
import net.nemerosa.ontrack.kdsl.spec.setProperty

var Project.autoValidationStampProperty: AutoValidationStampProperty?
    get() = getProperty(AUTO_VALIDATION_STAMP_PROPERTY)?.parse()
    set(value) {
        if (value != null) {
            setProperty(AUTO_VALIDATION_STAMP_PROPERTY, value)
        } else {
            deleteProperty(AUTO_VALIDATION_STAMP_PROPERTY)
        }
    }

const val AUTO_VALIDATION_STAMP_PROPERTY = "net.nemerosa.ontrack.extension.general.AutoValidationStampPropertyType"

data class AutoValidationStampProperty(
    val autoCreate: Boolean = true,
    val autoCreateIfNotPredefined: Boolean = false,
)
