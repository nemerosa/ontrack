package net.nemerosa.ontrack.extension.git.property

import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.docs.DocumentationField
import net.nemerosa.ontrack.model.structure.PropertySearchArguments
import net.nemerosa.ontrack.model.structure.ServiceConfiguration

class GitBranchConfigurationProperty(

    /**
     * Git branch or pull request ID
     */
    @APIDescription("Git branch or pull request ID")
    val branch: String,

    /**
     * Build link
     */
    @DocumentationField
    @APIDescription("How builds are linked to their Git commit")
    val buildCommitLink: ServiceConfiguration?,

    /**
     * Build overriding policy when synchronizing
     */
    @APIDescription("Build overriding policy when synchronizing")
    val override: Boolean,

    /**
     * Interval in minutes for build/tag synchronization
     */
    @APIDescription("Interval in minutes for build/tag synchronization")
    val buildTagInterval: Int

) {
    companion object {
        fun getSearchArguments(scmName: String): PropertySearchArguments? =
            if (scmName.isNotBlank()) {
                PropertySearchArguments(
                    jsonContext = null,
                    jsonCriteria = "pp.json->>'branch' = :scmName",
                    criteriaParams = mapOf("scmName" to scmName)
                )
            } else {
                null
            }
    }
}
