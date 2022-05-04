package net.nemerosa.ontrack.extension.github.ingestion.validation

import net.nemerosa.ontrack.graphql.support.TypeRef
import net.nemerosa.ontrack.model.annotations.APIDescription

/**
 * Common data between all the inputs
 */
abstract class AbstractGitHubIngestionValidateDataInput(
    @APIDescription("Name of the repository owner to target")
    val owner: String,
    @APIDescription("Name of the repository to target")
    val repository: String,
    // @APIDescription("GitHub ref to target (refs/heads/...)")
    // val ref: String,
    @APIDescription("Name of the validation stamp to create")
    val validation: String,
    @APIDescription("Validation data")
    @TypeRef(embedded = true)
    val validationData: GitHubIngestionValidationDataInput,
    @APIDescription("Optional validation status")
    val validationStatus: String?,
) {
    abstract fun toPayload(): GitHubIngestionValidateDataPayload
}
