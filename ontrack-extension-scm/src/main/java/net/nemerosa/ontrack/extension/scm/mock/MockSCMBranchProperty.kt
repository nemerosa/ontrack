package net.nemerosa.ontrack.extension.scm.mock

import net.nemerosa.ontrack.model.structure.PropertySearchArguments

data class MockSCMBranchProperty(
    val name: String,
) {
    companion object {

        fun getSearchArguments(token: String): PropertySearchArguments? =
            if (token.isNotBlank()) {
                PropertySearchArguments(
                    jsonContext = null,
                    jsonCriteria = "pp.json->>'name' = :name",
                    criteriaParams = mapOf("name" to token)
                )
            } else {
                null
            }

    }
}