package net.nemerosa.ontrack.kdsl.spec.extension.scm

import net.nemerosa.ontrack.json.getRequiredTextField
import net.nemerosa.ontrack.kdsl.spec.Project
import net.nemerosa.ontrack.kdsl.spec.deleteProperty
import net.nemerosa.ontrack.kdsl.spec.getProperty
import net.nemerosa.ontrack.kdsl.spec.setProperty

const val MOCK_SCM_PROJECT_PROPERTY =
    "net.nemerosa.ontrack.extension.scm.mock.MockSCMProjectPropertyType"

var Project.mockScmProjectProperty: String?
    get() = getProperty(MOCK_SCM_PROJECT_PROPERTY)?.getRequiredTextField("name")
    set(value) {
        if (value != null) {
            setProperty(MOCK_SCM_PROJECT_PROPERTY, mapOf("name" to value))
        } else {
            deleteProperty(MOCK_SCM_PROJECT_PROPERTY)
        }
    }
