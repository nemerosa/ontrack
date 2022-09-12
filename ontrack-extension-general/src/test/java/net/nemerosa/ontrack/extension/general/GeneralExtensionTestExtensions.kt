package net.nemerosa.ontrack.extension.general

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.Project

fun AbstractDSLTestSupport.autoValidationStampProperty(
    project: Project,
    autoCreate: Boolean = true,
    autoCreateIfNotPredefined: Boolean = false,
) {
    setProperty(
        project,
        AutoValidationStampPropertyType::class.java,
        AutoValidationStampProperty(
            isAutoCreate = autoCreate,
            isAutoCreateIfNotPredefined = autoCreateIfNotPredefined,
        )
    )
}