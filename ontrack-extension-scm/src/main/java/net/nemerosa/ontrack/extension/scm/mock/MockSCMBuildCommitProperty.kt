package net.nemerosa.ontrack.extension.scm.mock

import net.nemerosa.ontrack.model.structure.PropertySearchArguments

data class MockSCMBuildCommitProperty(
    val id: String,
) {
    companion object {
        fun getSearchArguments(token: String): PropertySearchArguments? =
            if (token.isNotBlank()) {
                PropertySearchArguments(
                    jsonContext = null,
                    jsonCriteria = "pp.json->>'id' = :token",
                    criteriaParams = mapOf("token" to token)
                )
            } else {
                null
            }
    }
}