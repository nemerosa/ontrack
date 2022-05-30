package net.nemerosa.ontrack.kdsl.spec.extension.git

import net.nemerosa.ontrack.kdsl.spec.Build
import net.nemerosa.ontrack.kdsl.spec.deleteProperty
import net.nemerosa.ontrack.kdsl.spec.getProperty
import net.nemerosa.ontrack.kdsl.spec.setProperty

const val GIT_COMMIT_PROPERTY = "net.nemerosa.ontrack.extension.git.property.GitCommitPropertyType"

var Build.gitCommitProperty: String?
    get() = getProperty(GIT_COMMIT_PROPERTY)?.path("commit")?.asText()
    set(value) {
        if (value != null) {
            setProperty(GIT_COMMIT_PROPERTY, mapOf("commit" to value))
        } else {
            deleteProperty(GIT_COMMIT_PROPERTY)
        }
    }
