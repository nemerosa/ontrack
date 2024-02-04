package net.nemerosa.ontrack.kdsl.spec.extension.scm

import net.nemerosa.ontrack.json.getRequiredTextField
import net.nemerosa.ontrack.kdsl.spec.*

const val MOCK_SCM_BUILD_COMMIT_PROPERTY =
    "net.nemerosa.ontrack.extension.scm.mock.MockSCMBuildCommitPropertyType"

var Build.mockScmBuildCommitProperty: String?
    get() = getProperty(MOCK_SCM_BUILD_COMMIT_PROPERTY)?.getRequiredTextField("id")
    set(value) {
        if (value != null) {
            setProperty(MOCK_SCM_BUILD_COMMIT_PROPERTY, mapOf("id" to value))
        } else {
            deleteProperty(MOCK_SCM_BUILD_COMMIT_PROPERTY)
        }
    }
